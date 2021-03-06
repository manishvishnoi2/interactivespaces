/*
 * Copyright (C) 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package interactivespaces.launcher.bootstrap;

import interactivespaces.system.core.logging.LoggingProvider;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Logging provider using Log4J.
 *
 * @author Keith M. Hughes
 */
public class Log4jLoggingProvider implements LoggingProvider {

  /**
   * Where the logging properties file is kept in lib/system/java. This is deprecated.
   */
  public static final String LOGGING_PROPERTIES_FILE_LIB_SYSTEM_JAVA = "lib/system/java/log4j.properties";

  /**
   * Where the logging properties file is kept in the config folder.
   */
  public static final String LOGGING_PROPERTIES_FILE_CONFIG = "system/log4j.properties";

  /**
   * The file path to the logeer for the Interactive Spaces container.
   */
  public static final String FILEPATH_CONTAINER_INTERACTIVESPACES_LOG = "logs/interactivespaces.log";

  /**
   * The Log4J property for specifying the file system location of the log folder.
   */
  public static final String LOG4J_PROPERTY_FILEPATH_INTERACTIVESPACES_LOGGER =
      "log4j.appender.interactivespaces.File";

  /**
   * The map of logging levels to their log4j level.
   */
  public static final Map<String, Level> LOG_4J_LEVELS;

  static {
    Map<String, Level> levels = new HashMap<String, Level>();
    levels.put(LOG_LEVEL_ERROR, Level.ERROR);
    levels.put(LOG_LEVEL_FATAL, Level.FATAL);
    levels.put(LOG_LEVEL_DEBUG, Level.DEBUG);
    levels.put(LOG_LEVEL_INFO, Level.INFO);
    levels.put(LOG_LEVEL_OFF, Level.OFF);
    levels.put(LOG_LEVEL_TRACE, Level.TRACE);
    levels.put(LOG_LEVEL_WARN, Level.WARN);

    LOG_4J_LEVELS = Collections.unmodifiableMap(levels);
  }

  /**
   * The root logger for the container.
   */
  private Logger baseInteractiveSpacesLogger;

  /**
   * The base log for the container.
   */
  private Log baseContainerLog;

  /**
   * Configure the provider.
   *
   * @param baseInstallDir
   *          base installation directory for IS
   * @param configDir
   *          the configuration directory for IS
   */
  public void configure(File baseInstallDir, File configDir) {
    File loggingPropertiesFile = findLoggingConfiguration(baseInstallDir, configDir);

    Properties loggingProperties = new Properties();

    FileInputStream fileInputStream = null;

    try {
      fileInputStream = new FileInputStream(loggingPropertiesFile);
      loggingProperties.load(fileInputStream);

      loggingProperties.put(LOG4J_PROPERTY_FILEPATH_INTERACTIVESPACES_LOGGER, new File(baseInstallDir,
          FILEPATH_CONTAINER_INTERACTIVESPACES_LOG).getAbsolutePath());
      PropertyConfigurator.configure(loggingProperties);
      baseInteractiveSpacesLogger = Logger.getLogger(LOGGER_BASE_NAME);

      baseContainerLog = new Log4JLogger(baseInteractiveSpacesLogger);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(String.format("Unable to find container configuration %s",
          loggingPropertiesFile.getAbsolutePath()));
    } catch (IOException e) {
      throw new RuntimeException(String.format("Error while reading container configuration %s",
          loggingPropertiesFile.getAbsolutePath()), e);
    }
  }

  /**
   * Locate the logging configuration for the container.
   *
   * @param baseInstallDir
   *          the base installation directory for the container
   * @param configDir
   *          the configuration directory for the container
   *
   * @return the logging configuration file
   */
  private File findLoggingConfiguration(File baseInstallDir, File configDir) {

    // TODO(keith): Sort out all config locations so can have a base folder for just configs that this can look into.
    File loggingPropertiesFile = new File(configDir, LOGGING_PROPERTIES_FILE_CONFIG);
    System.out.println(loggingPropertiesFile.getAbsolutePath());
    if (loggingPropertiesFile.isFile()) {
      return loggingPropertiesFile;
    }

    loggingPropertiesFile = new File(baseInstallDir, LOGGING_PROPERTIES_FILE_LIB_SYSTEM_JAVA);
    if (loggingPropertiesFile.isFile()) {
      return loggingPropertiesFile;
    }

    throw new RuntimeException("Could not locate log4j logging configuration file");
  }

  @Override
  public Log getLog() {
    return baseContainerLog;
  }

  @Override
  public Log getLog(String logName, String level) {
    Level l = LOG_4J_LEVELS.get(level.toLowerCase());
    boolean unknownLevel = false;
    if (l == null) {
      unknownLevel = true;
      l = Level.ERROR;
    }

    Logger logger = Logger.getLogger("interactivespaces." + logName);
    logger.setLevel(l);

    if (unknownLevel) {
      logger.error(String.format("Unknown log level %s, set to ERROR", level));
    }

    return new Log4JLogger(logger);
  }

  @Override
  public boolean modifyLogLevel(Log log, String level) {
    if (Log4JLogger.class.isAssignableFrom(log.getClass())) {

      Level l = LOG_4J_LEVELS.get(level.toLowerCase());
      if (l != null) {
        ((Log4JLogger) log).getLogger().setLevel(l);

        return true;
      } else {
        log.error(String.format("Unknown log level %s", level));
      }
    } else {
      log.error("Attempt to modify an unmodifiable logger");
    }

    return false;
  }
}
