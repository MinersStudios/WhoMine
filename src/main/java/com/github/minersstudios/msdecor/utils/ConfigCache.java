package com.github.minersstudios.msdecor.utils;

import com.github.minersstudios.msdecor.MSDecor;
import com.github.minersstudios.msdecor.customdecor.CustomDecorData;
import com.github.minersstudios.msdecor.customdecor.register.christmas.*;
import com.github.minersstudios.msdecor.customdecor.register.decorations.home.*;
import com.github.minersstudios.msdecor.customdecor.register.decorations.home.heads.DeerHead;
import com.github.minersstudios.msdecor.customdecor.register.decorations.home.heads.HoglinHead;
import com.github.minersstudios.msdecor.customdecor.register.decorations.home.heads.ZoglinHead;
import com.github.minersstudios.msdecor.customdecor.register.decorations.home.plushes.BMOPlush;
import com.github.minersstudios.msdecor.customdecor.register.decorations.home.plushes.BrownBearPlush;
import com.github.minersstudios.msdecor.customdecor.register.decorations.home.plushes.RacoonPlush;
import com.github.minersstudios.msdecor.customdecor.register.decorations.street.*;
import com.github.minersstudios.msdecor.customdecor.register.furniture.chairs.*;
import com.github.minersstudios.msdecor.customdecor.register.furniture.lamps.BigLamp;
import com.github.minersstudios.msdecor.customdecor.register.furniture.lamps.SmallLamp;
import com.github.minersstudios.msdecor.customdecor.register.furniture.nightstand.*;
import com.github.minersstudios.msdecor.customdecor.register.furniture.tables.BigTable;
import com.github.minersstudios.msdecor.customdecor.register.furniture.tables.SmallTable;
import com.github.minersstudios.msdecor.customdecor.register.halloween.SkeletonHand;
import com.github.minersstudios.msdecor.customdecor.register.other.Poop;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class ConfigCache {
	public final @NotNull File configFile;
	public final @NotNull YamlConfiguration configYaml;

	public final boolean isChristmas;
	public final boolean isHalloween;

	public final List<CustomDecorData> recipeDecors = new ArrayList<>();

	public ConfigCache() {
		this.configFile = new File(MSDecor.getInstance().getPluginFolder(), "config.yml");
		this.configYaml = YamlConfiguration.loadConfiguration(this.configFile);

		this.isChristmas = this.configYaml.getBoolean("is-christmas");
		this.isHalloween = this.configYaml.getBoolean("is-halloween");
	}

	public void registerCustomDecors() {
		//<editor-fold desc="Custom decors">
		new Ball().register(this.isChristmas);
		new TallBall().register(this.isChristmas);
		new SnowmanBall().register(this.isChristmas);
		new SnowflakeOnString().register(this.isChristmas);
		new StarOnString().register(this.isChristmas);
		new SantaSock().register(this.isChristmas);
		new Snowman().register(this.isChristmas);
		new TreeStar().register(this.isChristmas);

		new SkeletonHand().register(this.isHalloween);

		new Poop().register();

		new DeerHead().register();
		new HoglinHead().register();
		new ZoglinHead().register();

		new BMOPlush().register();
		new BrownBearPlush().register();
		new RacoonPlush().register();

		new Cell().register();
		new CookingPot().register();
		new OldCamera().register();
		new Patefon().register();
		new Piggybank().register();
		new SmallClock().register();
		new SmallGlobe().register();

		new ATM().register();
		new Brazier().register();
		new FireHydrant().register();
		new IronTrashcan().register();
		new Wheelbarrow().register();

		new BigLamp().register();
		new SmallLamp().register();

		new BigTable().register();
		new SmallTable().register();

		new BarStool().register();
		new CoolArmchair().register();
		new CoolChair().register();
		new Armchair().register();
		new Chair().register();
		new RockingChair().register();
		new SmallArmchair().register();
		new SmallChair().register();

		new AcaciaNightstand().register();
		new BirchNightstand().register();
		new CrimsonNightstand().register();
		new DarkOakNightstand().register();
		new JungleNightstand().register();
		new MangroveNightstand().register();
		new OakNightstand().register();
		new SpruceNightstand().register();
		new WarpedNightstand().register();
		//</editor-fold>
	}
}
