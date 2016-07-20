package sr.party;

import com.garbagemule.MobArena.MobArena;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import com.garbagemule.MobArena.MobArenaHandler;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;

/*
 * This Main Class file has:
 *  Ghost commands
 *  Shaman & Nightlord commands
 *  /classes and /clear commands
 *  And of course, all the Party commands.
 * 
 */

public class Party extends JavaPlugin
{
   static final Logger log = Logger.getLogger("Minecraft");
   public static Set<String> ghost = new HashSet<String>();
   
   public HashMap<String, Integer> party = new HashMap<String, Integer>();
   public HashMap<String, Integer> temphashmap = new HashMap<String, Integer>();
   public HashMap<String, Integer> invitedplayertime = new HashMap<String, Integer>();
   
   public HashSet<String> pcSpy = new HashSet<String>();
   
   
   public static File arenaFile;
   public static FileConfiguration arenas;
   
   public static MobArenaHandler maHandler;
   
   public String[] splice1;
   public String[] splice2;
   
   public String arenaplayer;
   public String arenatarget;
   
 
    public int a;
    
    public HashSet<String> queryDuel = new HashSet<String>();
    public HashSet<String> isDueling = new HashSet<String>();
    
    public HashSet<String> inArena2 = new HashSet<String>();
    
    public HashSet<Integer> inArena = new HashSet<Integer>();
    public HashMap<String, Integer> intArena = new HashMap<String, Integer>();
    
    public HashMap<String, String> paired = new HashMap<String, String>();
    public HashMap<String, String> paired2 = new HashMap<String, String>();
    
    public HashMap<String, Location> playerloc = new HashMap<String, Location>();
    
    public HashMap<String, Integer> money = new HashMap<String, Integer>();
    
    
    public boolean started = false;
    
    public int moneyint;
    
    public int duelIssued;
   
    public static HashMap<String, Integer> classtimer = new HashMap<String, Integer>(); // classes

   final Set<Integer> partynumber = new HashSet<Integer>();
   public HashMap<Integer, Integer> partysize = new HashMap<Integer, Integer>();
   public int MaxPartySize = 4;
   public HashSet<String> isLeader = new HashSet<String>();
   
   
   public Party plugin;   
   
    public static Economy econ = null;
    public static Permission perms = null;
    @SuppressWarnings("unused")
	private static Vault vault = null;
   
    public Warlock lock;
    
    public void setupMobArenaHandler()
    {
        Plugin maPlugin = (MobArena) Bukkit.getServer().getPluginManager().getPlugin("MobArena");

        if (maPlugin == null) 
        {
            return;
        }

        maHandler = new MobArenaHandler();
    }
    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
        {
            econ = economyProvider.getProvider();
        }
        return (econ != null);
    }
    
    private boolean setupPermissions() 
    {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
    }
   
    
    

   public void onEnable()
   {
       
       PluginDescriptionFile pdfFile = getDescription();
       
       // Mob Arena
       
       setupMobArenaHandler();
       
       // load PManager

       // Names.load();
       
       
       loadFiles();
       
       // Misc Loaded
       getServer().getPluginManager().registerEvents(new Durability(this), this);
       getServer().getPluginManager().registerEvents(new EnchantStuff(this), this);
       getServer().getPluginManager().registerEvents(new HealthBuffs(this), this);
      
       // PvP Class Loads    
       getServer().getPluginManager().registerEvents(new Archer(this), this);
       getServer().getPluginManager().registerEvents(new Assassin(this), this);
       getServer().getPluginManager().registerEvents(new ChaosCrusader(this), this);
       getServer().getPluginManager().registerEvents(new Gladiator(this), this);
       getServer().getPluginManager().registerEvents(new Martyr(this), this);
       getServer().getPluginManager().registerEvents(new Nightlord(this), this);
       getServer().getPluginManager().registerEvents(new Paladin(this), this);
       getServer().getPluginManager().registerEvents(new PL(this), this); // houses Berserker and Bard plus other misc stuff
       getServer().getPluginManager().registerEvents(new Pyromancer(this), this);
       getServer().getPluginManager().registerEvents(new Ranger(this), this);
       getServer().getPluginManager().registerEvents(new Scout(this), this);
       getServer().getPluginManager().registerEvents(new Skirmisher(this), this);
       getServer().getPluginManager().registerEvents(new Warlock(this), this);

       
       
   
       Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
       
           if (!setupEconomy() )
           {
               log.info(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
               getServer().getPluginManager().disablePlugin(this);
               return;
           }
           
           if ((setupEconomy()) && (econ != null)) 
            {
                vault = (Vault) x;

            } 
            else 
            {
               getPluginLoader().disablePlugin(this);
                return;
            }
           
           setupPermissions();
       
       

       Party.log.info("[" + pdfFile.getName() + "] (By cr0ssVtW, acidsin. Updated by Jxq) - v" + pdfFile.getVersion() + " loaded.");
   }

   public void onDisable()
	
   {  
       PluginDescriptionFile pdfFile = getDescription();
       Party.log.info("[" + pdfFile.getName() + "] (By cr0ssVtW, acidsin. Updated by Jxq) - v" + pdfFile.getVersion() + " unloaded.");
       
       
       /*
        * Clean up for Warlock Skeletons
        */
       
       lock.cleanUP();
       
       /*
        * Clean up for Pyro Balls
        */
        List<World> w = Bukkit.getWorlds();
        int amtbig = 0;
        
        for(World o : w)
        {

            for(Entity e : o.getEntities())
            {

                if ((e instanceof Fireball) || (e instanceof SmallFireball))
                {
                  e.remove();

                  amtbig += 1;
                }

            }

        }
        System.out.println("Amount of Fireballs removed: " + amtbig);
       
       /*
        * 
        */

       
       Iterator<Player> iterator = Nightlord.helmplayer.iterator();
        while (iterator.hasNext())
        {
            Player player = iterator.next();

        if (player.getInventory().getHelmet() != null)
           {

              if (Nightlord.oldhelm.containsKey(player))
                {
                   ItemStack helm2 = Nightlord.oldhelm.get(player);
                   Nightlord.oldhelm.remove(player);
                   Nightlord.helmplayer.remove(player);
                   player.getInventory().setHelmet(helm2);
                }

            }
        }
   }
   
   
   public void loadFiles()
   {
       Boolean exists = new File("plugins/SRParty").exists();
       
       if (!exists)
       {
           new File("plugins/SRParty").mkdir();
       }
       
       arenaFile = new File("plugins/SRParty/arenas.yml");
       
       if (!arenaFile.exists())
       {
           try
           {
               arenaFile.createNewFile();
           } catch (IOException e)
           {
               System.out.println("[SRParty] Failed to create arenas.yml - Check read/write perms.");
               e.printStackTrace();
           }
       }
       
       arenas = YamlConfiguration.loadConfiguration(arenaFile);
   }
   
    public void unStealth(Player player)
    {
        @SuppressWarnings("unused")
		CraftPlayer cplr = (CraftPlayer)player;

        for (Player other : Bukkit.getServer().getOnlinePlayers())
        {
          if ((!other.equals(player)) && (!other.canSee(player)))
          {
             other.showPlayer(player);
          }
            
        }
    }

    public void onStealth(Player player)
    {
        @SuppressWarnings("unused")
		CraftPlayer cplr = (CraftPlayer)player;

        for (Player other : Bukkit.getServer().getOnlinePlayers())
        {
          if ((!other.equals(player)) && (other.canSee(player)))
          {
            if (!other.hasPermission("assassin.see"))
            {
                other.hidePlayer(player);
            }
          }
        }
    }
   
    
   
   @SuppressWarnings("deprecation")
@Override
   public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
    {
        
        Player player = null;
        
        if (sender instanceof Player)
        {
            player = (Player)sender;
        }
        
        /*
         * MISC COMMANDS HERE
         */
        
        if (commandLabel.equalsIgnoreCase("classes"))
        {        
            Boolean oncooldown = false;
            int cooldown;

            if (classtimer.containsKey(player.getName()))
            {
                cooldown = getCurrentTime() - classtimer.get(player.getName());
                if (cooldown > 30 || cooldown < 0)
                {
                    oncooldown = false;
                }
                else
                {
                    oncooldown = true;
                    player.sendMessage(ChatColor.GOLD + "You can only check your class every 30 seconds.");
                }

            }

            if (oncooldown == true)
            {
                return true;
            }
            
            String owner = "Owner";
            String coowner = "CoOwner";
            String admin = "Admin";
            String jradmin = "JrAdmin";
            String srmod = "SrMod";
            String mod = "Mod";
            String eventmod = "EventMod";
            String chatmod = "ChatMod";
            String jrmod = "jrmod";
            String helper = "Helper";
            String memberplus = "MemberPlus";
            String membervip = "MemberVIP";
            String membervipplus = "MemberVIPPlus";
            String elite = "Elite";
            String hero = "Hero";
            String champion = "Champion";
            String lord = "Lord";
            String lady = "Lady";
            String king = "King";
            String queen = "Queen";
            String emperor = "Emperor";
            String empress = "Empress";
            String pvpvip = "PvPVIP";
            String pvpelite = "PvPElite";
            String pvphero = "PvPHero";
            String pvpchamp = "PvPChamp";
            String pvplord = "PvPLord";
            String pvpking = "PvPKing";
            String pvpgod = "PvPGod";
            String pvpsavage = "PvPSavage";
            String savage = "Savage";
            String savagegod = "SavageGOD";
            String member = "Member";

            classtimer.put(player.getName(), getCurrentTime());
             for (String s : Party.perms.getPlayerGroups(player))
             {
                 if (!s.equalsIgnoreCase(savage) && !s.equalsIgnoreCase(savagegod) && !s.equalsIgnoreCase(member)
                         && !s.equalsIgnoreCase(emperor) && !s.equalsIgnoreCase(empress) && !s.equalsIgnoreCase(king) && !s.equalsIgnoreCase(queen) 
                         && !s.equalsIgnoreCase(lord) && !s.equalsIgnoreCase(lady) && !s.equalsIgnoreCase(champion) && !s.equalsIgnoreCase(hero) && !s.equalsIgnoreCase(elite) 
                         && !s.equalsIgnoreCase(membervipplus) && !s.equalsIgnoreCase(membervip) && !s.equalsIgnoreCase(memberplus) && !s.equalsIgnoreCase(helper) && !s.equalsIgnoreCase(jrmod) 
                         && !s.equalsIgnoreCase(chatmod) && !s.equalsIgnoreCase(mod) && !s.equalsIgnoreCase(srmod) && !s.equalsIgnoreCase(jradmin) && !s.equalsIgnoreCase(admin) 
                         && !s.equalsIgnoreCase(coowner) && !s.equalsIgnoreCase(owner) && !s.equalsIgnoreCase(pvpvip) && !s.equalsIgnoreCase(pvpelite) && !s.equalsIgnoreCase(pvphero) 
                         && !s.equalsIgnoreCase(pvpchamp) && !s.equalsIgnoreCase(pvplord) && !s.equalsIgnoreCase(pvpking) && !s.equalsIgnoreCase(pvpgod) && !s.equalsIgnoreCase(pvpsavage)
                         && !s.equalsIgnoreCase(eventmod))
                 {
                     player.sendMessage(ChatColor.GOLD + "You are a member of the " + ChatColor.DARK_RED + s + ChatColor.GOLD + " Class");
                 }
             
             }
           
        }
        //
        
        
        if (commandLabel.equalsIgnoreCase("frameclear") &&(player.hasPermission("clear.use")))
        {
            int amtbig = 0;
            World world = player.getWorld();
            
            for(Entity e : world.getEntities())
            {
             
                if ((e instanceof ItemFrame))
                {
                  e.remove();

                  amtbig += 1;
                }
        
            }
            player.sendMessage(ChatColor.AQUA + "You removed " + amtbig + " Item Frames.");
            
        }
        
        if (commandLabel.equalsIgnoreCase("fireclear") &&(player.hasPermission("clear.use")))
        {
            int amtbig = 0;
            World world = player.getWorld();
            
            for(Entity e : world.getEntities())
            {
             
                if ((e instanceof Fireball) || (e instanceof SmallFireball))
                {
                  e.remove();

                  amtbig += 1;
                }
        
            }
            player.sendMessage(ChatColor.AQUA + "You removed " + amtbig + " Fireball Entities.");
            
        }
        
        
        
        
        
        
        
        
        
        
        // Remove Fire or Knockback from weapon
        
        if (commandLabel.equalsIgnoreCase("removeknockback"))
        {
            ItemStack item = player.getItemInHand();

            item.removeEnchantment(Enchantment.ARROW_KNOCKBACK);
            item.removeEnchantment(Enchantment.KNOCKBACK);
            player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.GREEN + "Removed Knockback Enchant from item.");
            return true;
        }
        
        if (commandLabel.equalsIgnoreCase("removefire"))
        {
            ItemStack item = player.getItemInHand();
            if (item.containsEnchantment(Enchantment.ARROW_FIRE))
            {
                item.removeEnchantment(Enchantment.ARROW_FIRE);
                player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.GREEN + "Removed Fire Enchant from item.");
            }
            else
            {
                player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.RED + "This item doesn't have a Fire Enchantment.");
                return true;
            }
            
            if (item.containsEnchantment(Enchantment.FIRE_ASPECT))
            {
                item.removeEnchantment(Enchantment.FIRE_ASPECT);
                player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.GREEN + "Removed Fire Enchant from item.");
            }
            else
            {
                player.sendMessage(ChatColor.GRAY + "[SR] " + ChatColor.RED + "This item doesn't have a Fire Enchantment.");
                return true;
            }
        }
        
        
        // REDO PARTY SYSTEM
        
        // PARTY SHIT:


        
        if (commandLabel.equalsIgnoreCase("pcspy"))
        {
            if (player.hasPermission("srparty.chat.spy"))
            {
                if (pcSpy.contains(player.getName()))
                {
                    pcSpy.remove(player.getName());
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "No longer spying on party chat.");
                }
                else
                {
                    pcSpy.add(player.getName());
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.GREEN + "Now spying on party chat.");
                }
            }
            else
            {
                return true;
            }
        }
        // Party chat
        
        if (commandLabel.equalsIgnoreCase("pc") && (player.hasPermission("srparty.chat")))
        {
            
          if (party.containsKey(player.getName().toLowerCase()))
          {
            int chatter = party.get(player.getName().toLowerCase());

            int stringsize = args.length;
            
            String chat;
            
            if (!(isLeader.contains(player.getName())))
            {
                chat = ("" + ChatColor.GRAY + "(" + ChatColor.AQUA + player.getDisplayName() + ChatColor.GRAY + ") ") + ChatColor.AQUA + "";
            }
            else
            {
                chat = ("" + ChatColor.GRAY + "(" + ChatColor.YELLOW + player.getDisplayName() + ChatColor.GRAY + ") ") + ChatColor.AQUA + "";
            }
            
                    
                    
            for (int i = 0; i < stringsize; i++)
            {
                
                String tempchat = args[i] + " ";
                
                chat = chat + tempchat;

            }
            
            
           for (Player partyplayer : getServer().getOnlinePlayers())
           {
               int partyplayers = 0;
               if (party.containsKey(partyplayer.getName().toLowerCase()))
               {
                   partyplayers = party.get(partyplayer.getName().toLowerCase());
               } 
               
               if (partyplayers == chatter || pcSpy.contains(partyplayer.getName()))
               {
                   if (pcSpy.contains(partyplayer.getName()))
                   {
                       partyplayer.sendMessage(ChatColor.GOLD + "["+ChatColor.DARK_GRAY+"SR"+ChatColor.GOLD+"Party] " + ChatColor.AQUA + "(PN: " + chatter + ") "  + chat);
                   }
                   else
                   {
                       partyplayer.sendMessage(ChatColor.GOLD + "["+ChatColor.DARK_GRAY+"SR"+ChatColor.GOLD+"Party] " + ChatColor.AQUA + chat);
                   }
               }
           }
         }
         else
         {
             player.sendMessage(ChatColor.RED + "You are not in a party.");
             return true;
         }
        }
        
       // Create 
        
        
       if ((commandLabel.equalsIgnoreCase("partycreate") || (commandLabel.equalsIgnoreCase("pcreate"))) && (player.hasPermission("srparty.create")))
       {
           
           if (!(party.containsKey(player.getName().toLowerCase())))
           {
            
               player.sendMessage(ChatColor.GOLD + "You've created a party and are now the leader. Invite players with /pinvite playername !");
               int hashsize = partynumber.size() + 1;
            
			   party.put(player.getName().toLowerCase(), hashsize);
               
               partynumber.add(hashsize);
               partysize.put(hashsize, 1);
               isLeader.add(player.getName());
               
               
           }
           else
           {
               player.sendMessage(ChatColor.RED + "You are already in a party.");
               return true;
               
               
           }

          
            
            
             
             
       }
               
       


        
      // Invite
       
      if ((commandLabel.equalsIgnoreCase("partyinvite") || (commandLabel.equalsIgnoreCase("pinvite"))) && (player.hasPermission("srparty.invite")))
      {
           
            
            // String[] invitedplayer = commandLabel.split(" ");
            
            if (party.containsKey(player.getName().toLowerCase()))
        {
            
        
          if (args.length > 0)
          {
              
          
            String invitedplayer = args[0].toLowerCase();
            String invitedplayer2 = args[0];
            
            int pcheck = party.get(player.getName().toLowerCase());
            int psize = partysize.get(pcheck) + 1;
            
            if (psize > MaxPartySize)
            {
                player.sendMessage(ChatColor.RED + "Your party is full or has invites pending. To cancel an invite, type: /partycancel PlayerNameYouInvited ");
                return true;
            }
            
            int partynumber2 = party.get(player.getName().toLowerCase());
            OfflinePlayer offlinePlayer = getServer().getOfflinePlayer(invitedplayer);
			
            if (party.containsKey(invitedplayer))
            {
                player.sendMessage(ChatColor.RED + "That player is already in a party.");
                return true;
            }
            
            if (temphashmap.containsKey(invitedplayer))
            {
                player.sendMessage(ChatColor.RED + "That player already has a pending party invite.");
                return true;
            }
            
            if (offlinePlayer.isOnline())
            {
                Player onlinePlayer = getServer().getPlayer(invitedplayer);
                
                onlinePlayer.sendMessage(ChatColor.GOLD + "" + player.getDisplayName() + ChatColor.AQUA + " has sent you a party invite.");
                onlinePlayer.sendMessage(ChatColor.AQUA + "Type " + ChatColor.GOLD + "/partyaccept " + ChatColor.AQUA + "or " + ChatColor.GOLD + "/partydeny " + ChatColor.AQUA + "to accept or deny this invite.");
            }
            else
            {
                player.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }
            
            
            int currenttime = getCurrentTime();
            
            temphashmap.put(invitedplayer, partynumber2);
            
            invitedplayertime.put(invitedplayer, currenttime);
            
            partysize.put(pcheck, psize);
            
            player.sendMessage(ChatColor.GOLD + "You have invited " + invitedplayer2 + " to your party." + " PN:" + partynumber2);
            // player.sendMessage(ChatColor.GOLD + "You have invited " + player.getDisplayName() + " to your party.");
            
            
            
            
            
          
             
             
         }
          else
          {
              player.sendMessage(ChatColor.RED + "Try using " + ChatColor.GOLD + "/partyinvite playername");
              return true;
          }
       }
        else
        {
            player.sendMessage(ChatColor.RED + "You must be in a party to invite other players.");
            return true; 
        }
      }
    
      
     
       
      
      // Accept
      
       if ((commandLabel.equalsIgnoreCase("partyaccept") || (commandLabel.equalsIgnoreCase("paccept"))) && (player.hasPermission("srparty.accept")))
       {

            

            if (temphashmap.containsKey(player.getName().toLowerCase()))
            {
                
                int currenttime = getCurrentTime();
                
                int timeinvited = invitedplayertime.get(player.getName().toLowerCase());
                
                int timedifference = (currenttime - timeinvited);
                
                
                if (timedifference < 60)
                {
                    int partynumber2 = temphashmap.get(player.getName().toLowerCase());
                    for (Player player2 : this.getServer().getOnlinePlayers())
                    {
                        if (player2 != player)
                        {
                            if (party.containsKey(player2.getName().toLowerCase()))
                            {
                              int temppartynumber = party.get(player2.getName().toLowerCase());

                              if (temppartynumber == partynumber2)
                              {
                                player2.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has joined the party.");
                              }
                            }
                        }
                    }
                     
                    temphashmap.remove(player.getName().toLowerCase());
                    invitedplayertime.remove(player.getName().toLowerCase());
                    
                    party.put(player.getName().toLowerCase(), partynumber2);
                    
                    player.sendMessage(ChatColor.GOLD + "You have joined the party.");
                    
                }
                else
                {
                    int partynumber2 = temphashmap.get(player.getName().toLowerCase());
                    int psize = partysize.get(partynumber2) - 1;
                    partysize.put(partynumber2, psize);
					
                    temphashmap.remove(player.getName().toLowerCase());
                    invitedplayertime.remove(player.getName().toLowerCase());
                    
                    
                    player.sendMessage(ChatColor.RED + "Your party invite has expired." + " " + psize + " PN: " + partynumber2);
                    return true;
                }
                
            }
            
            else
            {
                
                
                player.sendMessage(ChatColor.RED + "You have not been invited to a party.");
                return true;
            
            }

             
        }
    
     
       
       // Deny
       
      if ((commandLabel.equalsIgnoreCase("partydeny") || (commandLabel.equalsIgnoreCase("pdeny"))) && (player.hasPermission("srparty.deny")))
      {
             
            if (temphashmap.containsKey(player.getName().toLowerCase()))
            {
             
                int currenttime = getCurrentTime();
                               
                int timeinvited = invitedplayertime.get(player.getName().toLowerCase());
                
                int timedifference = (currenttime - timeinvited);
                
                
                if (timedifference < 60)
                {
                    
                    int partynumber = temphashmap.get(player.getName().toLowerCase());
					
		    int psize = partysize.get(partynumber) - 1;
					
                    partysize.put(partynumber, psize);
					
                    
                    temphashmap.remove(player.getName().toLowerCase());
                    invitedplayertime.remove(player.getName().toLowerCase());
                    
                    player.sendMessage(ChatColor.GREEN + "You have declined the party invite." + " " + psize + "  PN: " + partynumber);
                    
                    
                    
                }
                else
                {
                    temphashmap.remove(player.getName().toLowerCase());
                    invitedplayertime.remove(player.getName().toLowerCase());
                    
                    
                    player.sendMessage(ChatColor.RED + "Your party invite has expired.");
                    return true;
                }
                
                
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You have not been invited to a party.");
                return true;
                
            }
             
             
        }

      
      
      
      // Leave
      
     if ((commandLabel.equalsIgnoreCase("partyleave") || (commandLabel.equalsIgnoreCase("pleave"))) && (player.hasPermission("srparty.leave")))
      {
             
            if (party.containsKey(player.getName().toLowerCase()))
            {
              int partynumber2 = party.get(player.getName().toLowerCase());
              
              for (Player player2 : this.getServer().getOnlinePlayers())
              {
                     
                     
                if (player2 != player)
                {
                    if (party.containsKey(player2.getName().toLowerCase()))
                    {
                      int temppartynumber = party.get(player2.getName().toLowerCase());
                    
                      if (temppartynumber == partynumber2)
                      {
                          player2.sendMessage(player.getDisplayName() + ChatColor.GOLD + " has left the party.");
                      }
                       
                       
                    }
                }
              }
                
                
                
                int psize = partysize.get(partynumber2);
                partysize.put(partynumber2, psize - 1);
                
                if (isLeader.contains(player.getName()))
                {
                    isLeader.remove(player.getName());
                }
                
                party.remove(player.getName().toLowerCase());
                player.sendMessage(ChatColor.GOLD + "You have left the party.");
               
            }
            else
            {
                player.sendMessage(ChatColor.RED + "You are not in a party.");
                return true;
            }
             
             
             
        } // end leave
     
        if ((commandLabel.equalsIgnoreCase("pkick") || commandLabel.equalsIgnoreCase("partykick")) && (player.hasPermission("srparty.create")))
        {
            if (party.containsKey(player.getName().toLowerCase()))
            {
                if (isLeader.contains(player.getName()))
                {
                    if (args.length > 0)
                    {
                        String kickedplayer = args[0].toLowerCase();
                        String kickedplayer2 = args[0];
                        
                        if (party.containsKey(kickedplayer))
                        {
                            if (player.getName().equals(kickedplayer2))
                            {
                                player.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.RED + "You cannot kick yourself.");
                                return true;
                            }
                            
                            Player newPlayer = Bukkit.getPlayer(kickedplayer2);
                            
                            int newpnum = party.get(newPlayer.getName().toLowerCase());
                            int pnum = party.get(player.getName().toLowerCase()); 
                            
                            if (pnum == newpnum)
                            {
                                party.remove(newPlayer.getName().toLowerCase());
                                
                                for (Player partyplayer : Bukkit.getOnlinePlayers())
                                {
                                    if (party.containsKey(partyplayer.getName().toLowerCase()))
                                    {
                                        int pnum2 = party.get(partyplayer.getName().toLowerCase());
                                    
                                        if (pnum == pnum2)
                                        {
                                            partyplayer.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.GOLD 
                                                + newPlayer.getName() + ChatColor.RED + " has been kicked from the party.");
                                        }
                                    }
                                }
                                
                                newPlayer.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.RED + "You have been removed from " 
                                        + ChatColor.GOLD + player.getName() + ChatColor.RED + "'s party.");
                                
                                int psize = partysize.get(pnum);
                                
                                partysize.put(pnum, psize - 1);
                            }
                            else
                            {
                                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "That player doesn't exist or is not in your party.");
                                return true;
                            }
                        }
                        else
                        {
                            player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "That player doesn't exist or is not in your party.");
                            return true;
                        }

                    }
                }
                else
                {
                    player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are not the party leader.");
                    return true;
                }
            }
            else
            {
                player.sendMessage(ChatColor.DARK_GRAY + "[SR] " + ChatColor.RED + "You are not in a party.");
                return true;
            }
        } // end kick
        
        if (commandLabel.equalsIgnoreCase("partycancel") || commandLabel.equalsIgnoreCase("pcancel"))
        {
            if (player.hasPermission("srparty.invite"))
            {
                if (party.containsKey(player.getName().toLowerCase()))
                {
                    if (args.length > 0)
                    {
                        String kickedplayer = args[0].toLowerCase();
                        String kickedplayer2 = args[0];
                        
                        if (temphashmap.containsKey(kickedplayer))
                        {
                            Player newPlayer = Bukkit.getPlayer(kickedplayer2);
                            
                            int pnum = temphashmap.get(newPlayer.getName().toLowerCase());
                            int psize = partysize.get(pnum);
                            
                            partysize.put(pnum, psize - 1);
                            
                            temphashmap.remove(newPlayer.getName().toLowerCase());
                            invitedplayertime.remove(newPlayer.getName().toLowerCase());
                            
                            for (Player partyplayer : Bukkit.getOnlinePlayers())
                            {
                                if (party.containsKey(partyplayer.getName().toLowerCase()))
                                {
                                    int pnum2 = party.get(partyplayer.getName().toLowerCase());
                                    
                                    if (pnum == pnum2)
                                    {
                                        partyplayer.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.GOLD 
                                                + newPlayer.getName() + "'s " + ChatColor.RED + "invite has been cancelled.");
                                    }
                                }
                            }
                            
                            newPlayer.sendMessage(ChatColor.GOLD + "["+ ChatColor.DARK_GRAY + "SR" + ChatColor.GOLD + "] " + ChatColor.GOLD + player.getName() 
                                    + ChatColor.RED + " has cancelled your invite.");
                        }
                    }
                }
            }
            
        }
     
    
        
        return true;
        
    }
    
   
 
    
    
     public int getCurrentTime()
     {
		Calendar calendar = new GregorianCalendar();

		int hour = calendar.get(Calendar.HOUR) * 3600;
		int minute = calendar.get(Calendar.MINUTE) * 60;
		int second = calendar.get(Calendar.SECOND);

		return hour + minute + second;
     }
    
    
    public static boolean isInteger(String s) 
    {
        try
        {
            Integer.parseInt(s);
        } catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }
    
    public static boolean isShort(String s) 
    {
        try
        {
            Short.parseShort(s);
        } catch (NumberFormatException e)
        {
            return false;
        }
        return true;
    }
    
}
