package com.gmail.dejayyy.hardcore;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class dbMain extends JavaPlugin implements Listener {

	public static File configFile;
	public static File pluginFolder;
	public FileConfiguration playersFile;
	
	
	public void onEnable(){
		
		this.getServer().getPluginManager().registerEvents(this,  this);
		
		this.saveDefaultConfig();
		this.loadPlayerYML();
		
	}
	
	public void onDisable(){
		
		
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String cmdL, String[] args)  {
		
		if(!(sender instanceof Player)){
			
			if(cmdL.equalsIgnoreCase("deathban") || cmdL.equalsIgnoreCase("db")){
				
				if(args[0].equalsIgnoreCase("unban")){
					
					if(this.playersFile.contains(args[1].toLowerCase())){
						
						this.getServer().getOfflinePlayer(args[1].toLowerCase()).setBanned(false);
							
						this.playersFile.set(args[1].toLowerCase(), null);
							
						this.savePlayerYML();
						
						sender.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "Player removed from DeathBan list.");
						
						return true;
							
					}else{
							
						sender.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "Player not on the DeathBan list.");
							
						return true;
						
					}
					
				}else{
					
					sender.sendMessage("You goofball, you cant run that command from console!");
					
					return true;
					
				}
			}
			
		}
		
		
			Player player = (Player) sender;
			
			if(cmdL.equalsIgnoreCase("deathban") || cmdL.equalsIgnoreCase("db")){
				
				if(!(player.hasPermission("deathban.admin"))){
					
					player.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "You don't have permission to run that command!");
				
					return true;
									
				}
				
				
				if(args.length == 1){
					
					if(args[0].equalsIgnoreCase("reload")){
							
						this.reloadConfig();
							
						player.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "DeathBan config reloaded.");
							
						return true;
							
					}else{
							
						player.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "Invalid Arguments!");
							
					}
					
				}else if(args.length == 2){
					
					if(args[0].equalsIgnoreCase("unban")){						
							
						if(this.playersFile.contains(args[1].toLowerCase())){
								
							this.getServer().getOfflinePlayer(args[1].toLowerCase()).setBanned(false);
								
							this.playersFile.set(args[1].toLowerCase(), null);
								
							this.savePlayerYML();
							
							player.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "Player removed from DeathBan list.");
							
							return true;
								
						}else{
								
							player.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "Player not on the DeathBan list.");
								
							return true;
							
						}
						
					}else{
						
						player.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "Invalid Arguments!"); 
						
					} //args = unban
					
				}else{
					
					player.sendMessage(ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA + "Invalid Arguments!");
					
				}	//args == 2
				
			} //cmdL
		
		
		return true;
		
	}
	
	
	@EventHandler
	public void playerLogin(PlayerLoginEvent event){
		
		Player player = event.getPlayer();
		
		long curTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis());
		
		long playerBan = this.playersFile.getLong(player.getName().toLowerCase());
		
		
		if(this.playersFile.contains(player.getName().toLowerCase())){
			
			if(curTime >= playerBan){
				
				player.setBanned(false);
				
				event.allow();
			
				this.playersFile.set(player.getName().toLowerCase(), null);
				
				this.savePlayerYML();
				
			}else{
				
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER,ChatColor.DARK_AQUA + "[DeathBan] " + ChatColor.AQUA +  "Try again in " + (playerBan - curTime) + " minute(s).");
				
			}//check ban time
			
		} //playersfile.contains
		
	} //player login
	
	@EventHandler
	public void playerDeath(PlayerDeathEvent event){
		
		String banMSG = this.getConfig().getString("banMSG");
		
		int banTime = this.getConfig().getInt("banTime");
		
		Player player = event.getEntity();
		
		if(!(player.hasPermission("deathban.exempt"))){
			
			this.playersFile.set(player.getName().toLowerCase(), TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis()) + banTime);
			
			player.setBanned(true);
			player.kickPlayer(banMSG);
			
			this.savePlayerYML();
			
		} //perm check
		
	} //death event
	
	public void savePlayerYML(){
		
		try {
			playersFile.save(configFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	} //save player file
	
	public void loadPlayerYML(){
		pluginFolder = getDataFolder();
	    configFile = new File(getDataFolder(), "players.yml");
	    
	    playersFile = new YamlConfiguration();
	    
		if(getDataFolder().exists() == false){
			
			try{
				getDataFolder().mkdir();
			}catch (Exception ex){
				//something went wrong.
			}
			
		} //plugin folder exists
	
	
		if(configFile.exists() == false){
			
			try{
				configFile.createNewFile();
			}catch (Exception ex){
				//something went wrong.
			}
		} //Configfile exist's
		
		try{ //Load payers.yml
			playersFile.load(configFile);
		}catch (Exception ex){
			//Something went wrong
		} //end try/catch
		
	} //load playerYML
	
}
