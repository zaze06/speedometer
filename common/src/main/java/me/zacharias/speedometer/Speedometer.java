package me.zacharias.speedometer;

import dev.architectury.platform.Platform;
import dev.architectury.utils.Env;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;

public class Speedometer
{
  public static final String MOD_ID = "speedometer";
  public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
  public static final String VERSION = Platform.getMod(MOD_ID).getVersion();
  public static SpeedometerIcon ICON = null;

  public static void init() {
    LOGGER.info("Loading speedometer by Allen");


    if(Platform.getEnvironment() != Env.CLIENT) {
      LOGGER.error("You're running speedometer on something other then a client, this is not supported");
      try {
        for (File f : Objects.requireNonNull(Platform.getModsFolder().toFile().listFiles())) {
          if (f.getName().startsWith("speedometer")) {
            String fileName = "speedometer-" + VERSION + ".jar.disable";
            if(f.renameTo(new File(f.getParent(), fileName))){
              LOGGER.warn("Successfully in renaming the mod jar file to {}", fileName);
              LOGGER.warn("You should remove the file from {}", Platform.getModsFolder().toString());
            }else{
              LOGGER.warn("Unsuccessful in renaming mod jar");
              LOGGER.warn("You should remove the mod from {} to no longer receive this message", Platform.getModsFolder().toString());
            }
          }
        }
      }catch (NullPointerException e){
        LOGGER.warn("Can't disable the mod. Please delete the file!");
      }
      return;
    }

    Client.init();
  }
  
  public static void loadSpeedometers(ResourceManager resourceManager)
  {
    Optional< Resource > resource = resourceManager.getResource(ResourceLocation.fromNamespaceAndPath(MOD_ID, "models/speedometer.json"));
    
    if(resource.isEmpty())
    {
      Config.setDisableVisualSpeedometer(true);
      LOGGER.error("Failed to load speedometer config");
      return;
    }
    
    try {
      BufferedReader stream = resource.get().openAsReader();
      String tmp;
      StringBuilder builder = new StringBuilder();
      while ((tmp = stream.readLine()) != null) {
        builder.append(tmp);
      }
      JSONObject data = new JSONObject(builder.toString());
      ICON = new SpeedometerIcon(data, resourceManager);
    }
    catch (Exception e){
      Config.setDisableVisualSpeedometer(true);
      LOGGER.error("Failed to load speedometer config", e);
      return;
    }
    
    LOGGER.info("Successfully loaded speedometer config from {}", resource.get().source().packId());
  }
}
