package org.jboss.forge.parser.spi;

public interface IndexRepository
{

   public int add(String jarName, boolean forceUpdate, String projectName);

   public int addClass(String className, String projectName);

   public int verify(String jarName, String projectName);

   public boolean reset(String projectName);

}
