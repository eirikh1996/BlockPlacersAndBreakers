package io.github.eirikh1996.blockplacersandbreakers.listener;

import br.net.fabiozumbi12.RedProtect.Bukkit.RedProtect;
import br.net.fabiozumbi12.RedProtect.Bukkit.Region;
import io.github.eirikh1996.blockplacersandbreakers.BlockPlacersAndBreakers;
import io.github.eirikh1996.blockplacersandbreakers.Settings;
import io.github.eirikh1996.blockplacersandbreakers.events.BlockBreakerBreakBlockEvent;
import io.github.eirikh1996.blockplacersandbreakers.events.BlockPlacerPlaceBlockEvent;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockBreaker;
import io.github.eirikh1996.blockplacersandbreakers.objects.BlockPlacer;
import io.github.eirikh1996.blockplacersandbreakers.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Dispenser;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

import static io.github.eirikh1996.blockplacersandbreakers.Messages.BPB_PREFIX;
import static io.github.eirikh1996.blockplacersandbreakers.Messages.ERROR;

public class BlockListener implements Listener {
    private static final ArrayList<Material> MINERAL_ORES = new ArrayList<>();
    private static final ArrayList<Material> CROPS = new ArrayList<>();
    private final BlockPlacersAndBreakers bpb = BlockPlacersAndBreakers.getInstance();
    static {
        MINERAL_ORES.add(Material.DIAMOND_ORE);
        MINERAL_ORES.add(Material.REDSTONE_ORE);
        MINERAL_ORES.add(Material.LAPIS_ORE);
        MINERAL_ORES.add(Material.EMERALD_ORE);
        MINERAL_ORES.add(Material.COAL_ORE);
        if (Settings.is1_13) {
            MINERAL_ORES.add(Material.NETHER_QUARTZ_ORE);
        } else {
            MINERAL_ORES.add(Material.getMaterial("QUARTZ_ORE"));
        }
        CROPS.add(Material.BEETROOT_SEEDS);
        CROPS.add(Material.MELON_SEEDS);
        CROPS.add(Material.PUMPKIN_SEEDS);
        if (Settings.is1_13){
            CROPS.add(Material.CARROT);
            CROPS.add(Material.POTATO);
            CROPS.add(Material.WHEAT_SEEDS);
        } else {
            CROPS.add(Material.getMaterial("POTATO_ITEM"));
            CROPS.add(Material.getMaterial("CARROT_ITEM"));
            CROPS.add(Material.getMaterial("SEEDS"));
        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDispense(BlockDispenseEvent event){
        final ItemStack dispensed = event.getItem();
        if (!event.getBlock().getType().equals(Material.DISPENSER)){
            return;
        }

        Dispenser d = (Dispenser) event.getBlock().getState();
        BlockFace face;
        if (Settings.is1_13){
            org.bukkit.block.data.type.Dispenser disp = (org.bukkit.block.data.type.Dispenser) event.getBlock().getBlockData();
            face = disp.getFacing();
        } else {
            org.bukkit.material.Dispenser disp = (org.bukkit.material.Dispenser) d.getData();
            face = disp.getFacing();
        }
        if (dispensed == null)
            return;

        final Block b = event.getBlock().getRelative(face);
        BlockBreaker breaker = BlockBreaker.at(d.getLocation());
        if (BlockPlacersAndBreakers.getInstance().getBlockBreakers().contains(breaker)){
            assert breaker != null;
            event.setCancelled(true);
            if (!dispensed.getType().name().endsWith("PICKAXE"))
                return;
            if (!b.getType().isBlock()){
                return;
            }
            final Damageable dmg = (Damageable) dispensed.getItemMeta();


            List<ItemStack> replace = new ArrayList<>();
            for (ItemStack i : d.getInventory().getContents()) {
                if (i == null)
                    continue;
                replace.add(i);
            }
            final ArrayList<ItemStack> drops = new ArrayList<>(b.getDrops());
            final Set<ItemStack> newDrops = new HashSet<>();
            boolean silkTouch = false;
            for (Enchantment ench : dispensed.getItemMeta().getEnchants().keySet()){
                int level = dispensed.getEnchantmentLevel(ench);
                if (ench.equals(Enchantment.SILK_TOUCH)){
                    final Material dropType = b.getType();
                    newDrops.add(new ItemStack(dropType));
                    silkTouch = true;
                }
                if (ench.equals(Enchantment.LOOT_BONUS_BLOCKS) && MINERAL_ORES.contains(b.getType())){
                    Random rand = new Random();
                    Material dropType = drops.get(0).getType();
                    int dropMultiplier;
                    if (level == 1){
                        dropMultiplier = 1 + rand.nextInt(3);
                    } else if (level == 2){
                        dropMultiplier = 2 + rand.nextInt(2);
                    } else {
                        dropMultiplier = 3 + rand.nextInt(2);
                    }

                    for (int i = 1 ; i <= dropMultiplier ; i++){
                        newDrops.add(new ItemStack(dropType));
                    }
                }

            }
            //Call event
            BlockBreakerBreakBlockEvent breakBlockEvent = new BlockBreakerBreakBlockEvent(b, BlockBreaker.at(d.getLocation()));
            Bukkit.getServer().getPluginManager().callEvent(breakBlockEvent);
            if (breakBlockEvent.isCancelled()){
                Player owner = Bukkit.getPlayer(breakBlockEvent.getBlockBreaker().getOwner());
                if (owner != null && breakBlockEvent.getCancellationMessage().length() > 0){
                    owner.sendMessage(breakBlockEvent.getCancellationMessage());
                }
                return;
            }
            if (silkTouch){
                b.setType(Material.AIR);
            } else {
                b.breakNaturally();
            }

            if (!newDrops.isEmpty()){
                for (ItemStack drop : newDrops){
                    b.getWorld().dropItemNaturally(b.getLocation(), drop);
                }
            }


    if (Settings.ApplyDamageToBreakerPickaxe) {
    new BukkitRunnable() {
        @Override
        public void run() {
            dmg.setDamage(dmg.getDamage() + 1);
            dispensed.setItemMeta((ItemMeta) dmg);
            Dispenser dispenser = (org.bukkit.block.Dispenser) event.getBlock().getState();
            dispenser.getInventory().clear();
            int maxDamage = 0;
            switch (dispensed.getType()) {
                case DIAMOND_PICKAXE:
                    maxDamage = 1562;
                    break;
                case GOLDEN_PICKAXE:
                    maxDamage = 33;
                    break;
                case IRON_PICKAXE:
                    maxDamage = 251;
                    break;
                case STONE_PICKAXE:
                    maxDamage = 132;
                    break;
                case WOODEN_PICKAXE:
                    maxDamage = 60;
                    break;
            }
            if (((Damageable) dispensed.getItemMeta()).getDamage() < maxDamage) {
                dispenser.getInventory().addItem(dispensed);
            }
            for (ItemStack i : replace)
                dispenser.getInventory().addItem(i);
        }
    }.runTask(BlockPlacersAndBreakers.getInstance());
}


        }

        if (BlockPlacersAndBreakers.getInstance().getBlockPlacers().contains(BlockPlacer.at(d.getLocation()))){

            if (!b.getType().name().endsWith("AIR")){
                Player owner = Bukkit.getPlayer(BlockPlacer.at(d.getLocation()).getOwner());
                if (owner != null){
                    owner.sendMessage(BPB_PREFIX + ERROR + "Block placers must not be obstructed");
                }
                return;
            }
            boolean place = false;
            Material dispensedType = dispensed.getType();
            if (dispensedType.isBlock()){
                if (dispensedType.name().endsWith("CONCRETE_POWDER") && (b.getRelative(BlockFace.EAST).getType() == Material.WATER||b.getRelative(BlockFace.WEST).getType() == Material.WATER||b.getRelative(BlockFace.SOUTH).getType() == Material.WATER||b.getRelative(BlockFace.NORTH).getType() == Material.WATER)){
                    dispensedType = Material.getMaterial(dispensedType.name().replace("_POWDER",""));

                }
                place = true;

            } else if (CROPS.contains(dispensedType)){//Place crops if it dispenses seeds/carrots/potatoes
                if (dispensedType.equals(Settings.is1_13 ? Material.WHEAT_SEEDS : Material.getMaterial("SEEDS"))){
                    dispensedType = Settings.is1_13 ? Material.WHEAT : Material.getMaterial("CROPS");
                }
                else if (dispensedType.equals(Settings.is1_13 ? Material.CARROT : Material.getMaterial("CARROT_ITEM"))){
                    dispensedType = Settings.is1_13 ? Material.CARROTS : Material.getMaterial("CARROT");
                }
                else if (dispensedType.equals(Settings.is1_13 ? Material.POTATO : Material.getMaterial("POTATO_ITEM"))){
                    dispensedType = Settings.is1_13 ? Material.POTATOES: Material.getMaterial("POTATO");
                }
                else if (dispensedType.equals(Material.BEETROOT_SEEDS)){
                    dispensedType = Settings.is1_13 ? Material.BEETROOTS : Material.getMaterial("BEETROOT_BLOCK");
                }
                else if (dispensedType.equals(Material.PUMPKIN_SEEDS)){
                    dispensedType = Material.PUMPKIN_STEM;
                }
                else if (dispensedType.equals(Material.MELON_SEEDS)){
                    dispensedType = Material.MELON_STEM;
                }
                place = true;
            } 
            if (!place){
                return;
            }
            event.setCancelled(true);

            final Material toPlace = dispensedType;
            //call event
            BlockPlacerPlaceBlockEvent placeBlockEvent = new BlockPlacerPlaceBlockEvent(b, BlockPlacer.at(d.getLocation()));
            Bukkit.getServer().getPluginManager().callEvent(placeBlockEvent);
            if (placeBlockEvent.isCancelled()){
                Player owner = Bukkit.getPlayer(placeBlockEvent.getBlockPlacer().getOwner());
                if (owner != null && placeBlockEvent.getCancellationMessage().length() > 0){
                    owner.sendMessage(placeBlockEvent.getCancellationMessage());
                }
                return;
            }
            new BukkitRunnable() {
                @Override
                public void run() {
                    b.setType(toPlace);
                    for (final ItemStack items : d.getInventory().getContents()) {
                        if (items == null)
                            continue;
                        if (!items.getType().equals(dispensed.getType()))
                            continue;
                        final int amount = items.getAmount();
                        if (amount == 1)
                            d.getInventory().remove(items);
                        else
                            items.setAmount(amount - 1);
                        break;
                    }


                }
            }.runTask(BlockPlacersAndBreakers.getInstance());


        }

    }
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        if (!(event.getBlock().getState() instanceof Dispenser)){
            return;
        }
        Dispenser d = (Dispenser) event.getBlock().getState();
        BlockBreaker breaker = BlockBreaker.at(d.getLocation());
        BlockPlacer placer = BlockPlacer.at(d.getLocation());
        if (BlockPlacersAndBreakers.getInstance().getBlockBreakers().contains(breaker)){
            assert breaker != null;
            if (!event.getPlayer().getUniqueId().equals(breaker.getOwner()) && !event.getPlayer().hasPermission("bpb.blockbreaker.breakothers")){
                event.getPlayer().sendMessage(BPB_PREFIX + ERROR + "You cannot break block breakers that belong to other players!");
                event.setCancelled(true);
                return;
            }
            event.getPlayer().sendMessage(BPB_PREFIX + "Block breaker broken");
            BlockPlacersAndBreakers.getInstance().getBlockBreakers().remove(breaker);
        }
        if (BlockPlacersAndBreakers.getInstance().getBlockPlacers().contains(placer)){
            assert placer != null;
            if (!event.getPlayer().getUniqueId().equals(placer.getOwner()) && !event.getPlayer().hasPermission("bpb.blockbreaker.breakothers")){
                event.getPlayer().sendMessage(BPB_PREFIX + ERROR + "You cannot break block breakers that belong to other players!");
                event.setCancelled(true);
                return;
            }
            event.getPlayer().sendMessage(BPB_PREFIX + "Block placer broken");
            BlockPlacersAndBreakers.getInstance().getBlockPlacers().remove(placer);
        }
        BlockPlacersAndBreakers.getInstance().updatePBFile();


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event){
        if (!event.getBlockPlaced().getType().equals(Material.DISPENSER)){
            return;
        }
        Dispenser d = (Dispenser) event.getBlockPlaced().getState();
        BlockFace face;
        if (Settings.is1_13){
            org.bukkit.block.data.type.Dispenser disp = (org.bukkit.block.data.type.Dispenser) d.getBlockData();
            face = disp.getFacing();
        } else {
            org.bukkit.material.Dispenser disp = (org.bukkit.material.Dispenser) d.getData();
            face = disp.getFacing();
        }
        Location relative = d.getBlock().getRelative(face).getLocation();
        if (BlockPlacersAndBreakers.getInstance().getRedProtectPlugin() != null){
            RedProtect rp = BlockPlacersAndBreakers.getInstance().getRedProtectPlugin();
            Region rg = rp.getAPI().getRegion(relative);
            if (!rg.canBuild(event.getPlayer())){
                event.getPlayer().sendMessage(BPB_PREFIX + ERROR + "Block placers or breakers cannot place nor break blocks in this RedProtect region!");
                event.setCancelled(true);
            }
        }
        else if (BlockPlacersAndBreakers.getInstance().getWorldGuardPlugin() != null){
            if (!WorldGuardUtils.allowedToBuild(event.getPlayer(), relative)){
                event.getPlayer().sendMessage(BPB_PREFIX + ERROR + "Block placers or breakers cannot place nor break blocks in this WorldGuard region!");
                event.setCancelled(true);
            }
        }
        else if (bpb.getGriefPreventionPlugin() != null){
            if (bpb.getGriefPreventionPlugin().allowBuild(event.getPlayer(), relative) != null){
                event.getPlayer().sendMessage(BPB_PREFIX + ERROR + "Block placers or breakers cannot place nor break blocks in this GriefPrevention claim!");
                event.setCancelled(true);
            }
        }
    }
}
