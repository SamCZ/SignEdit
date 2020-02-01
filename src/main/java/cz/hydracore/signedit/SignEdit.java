package cz.hydracore.signedit;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Campfire;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SignEdit extends JavaPlugin implements Listener {

    private Map<UUID, String[]> copiedLineMap = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            return false;
        }

        Player player = (Player) sender;

        player.sendMessage(command.getName() + " /" + label);

        if(command.getName().equals("sge")) {
            return onSetLineCommand(player, args);
        }

        if(command.getName().equals("sgcp")) {
            return onSignCopy(player, args);
        }

        if(command.getName().equals("sgpst")) {
            return onSignPaste(player, args);
        }

        return super.onCommand(sender, command, label, args);
    }

    private boolean onSetLineCommand(Player player, String[] args) {
        if(args.length <= 1)
            return false;

        int lineIndex = -1;

        try {
            lineIndex = Integer.parseInt(args[0]);
        } catch (Exception ex) {}

        if(lineIndex < 0) {
            player.sendMessage(ChatColor.RED + "You need to specify line index !");
            return false;
        }

        Block block = player.getTargetBlockExact(5);

        if(block == null) {
            return false;
        }

        BlockState state = block.getState();

        if(!(state instanceof Sign)) {
            player.sendMessage(ChatColor.RED + "You need to look at sign first !");
            return false;
        }

        Sign sign = (Sign) block.getState();

        String lineStr = "";

        for (int i = 1; i < args.length; i++) {
            lineStr += args[i] + " ";
        }

        lineStr = lineStr.substring(0, lineStr.length()-1);

        sign.setLine(lineIndex, ChatColor.translateAlternateColorCodes('&', lineStr));

        sign.update();

        return true;
    }

    private boolean onSignCopy(Player player, String[] args) {
        Block block = player.getTargetBlockExact(5);

        if(block == null) {
            return false;
        }

        BlockState state = block.getState();

        if(!(state instanceof Sign)) {
            player.sendMessage(ChatColor.RED + "You need to look at sign first !");
            return false;
        }

        Sign sign = (Sign) block.getState();

        this.copiedLineMap.put(player.getUniqueId(), sign.getLines().clone());

        player.sendMessage(ChatColor.GREEN + "Sign has been copied");

        return true;
    }

    private boolean onSignPaste(Player player, String[] args) {
        Block block = player.getTargetBlockExact(5);

        if(block == null) {
            return false;
        }

        BlockState state = block.getState();

        if(!(state instanceof Sign)) {
            player.sendMessage(ChatColor.RED + "You need to look at sign first !");
            return false;
        }

        Sign sign = (Sign) block.getState();

        if(!this.copiedLineMap.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "You dont have any copied signs !");
            return false;
        }

        String[] signLine = this.copiedLineMap.get(player.getUniqueId());

        for (int i = 0; i < 4; i++) {
            sign.setLine(i, signLine[i]);
        }

        sign.update();

        return true;
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if(!player.isOp()) {
            return;
        }

        Block block = event.getClickedBlock();

        if(block == null)
            return;

        BlockState state = block.getState();

        if(!(state instanceof Sign))
            return;

        Sign sign = (Sign) block.getState();

        for (int i = 0; i < 4; i++) {
            sign.setLine(i, ChatColor.translateAlternateColorCodes('&', sign.getLine(i)));
        }

        sign.update();
    }

    // Not working IDK why
    @EventHandler(ignoreCancelled = true)
    public void onSignChange(SignChangeEvent event) {
        Block block = event.getBlock();

        Sign sign = (Sign) block.getState();

        for (int i = 0; i < 4; i++) {
            sign.setLine(i, ChatColor.translateAlternateColorCodes('&', sign.getLine(i)));
        }

        sign.update();
    }
}
