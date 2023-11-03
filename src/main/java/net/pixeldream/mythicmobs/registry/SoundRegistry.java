package net.pixeldream.mythicmobs.registry;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static net.pixeldream.mythicmobs.MythicMobs.MOD_ID;

public class SoundRegistry {
    public static SoundEvent DRAKE_ROAR;
    public static SoundEvent DRAKE_DEATH;
    public static SoundEvent ROBOTIC_VOICE;

    public SoundRegistry() {
        DRAKE_ROAR = registerSoundEvent("drake_roar");
        DRAKE_DEATH = registerSoundEvent("drake_death");
        ROBOTIC_VOICE = registerSoundEvent("robotic_voice");
    }

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = new Identifier(MOD_ID, name);
        return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
    }
}
