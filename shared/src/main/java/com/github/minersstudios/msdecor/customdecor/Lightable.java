package com.github.minersstudios.msdecor.customdecor;

@SuppressWarnings("unused")
public interface Lightable extends CustomDecorData {
	int getFirstLightLevel();

	void setFirstLightLevel(int level);

	int getSecondLightLevel();

	void setSecondLightLevel(int level);
}
