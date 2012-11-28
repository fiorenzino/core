package org.jboss.forge.indexer.util;

import java.io.Serializable;

public class ClassInfo implements Serializable
{
   private static final long serialVersionUID = 1L;
   private String className;
   private String jarName;

   private String packageName;

   public ClassInfo()
   {
      // TODO Auto-generated constructor stub
   }

   public ClassInfo(String className, String jarName, String packageName)
   {
      this.className = className;
      this.jarName = jarName;
      this.packageName = packageName;
   }

   public String getClassName()
   {
      return className;
   }

   public void setClassName(String className)
   {
      this.className = className;
   }

   public String getJarName()
   {
      return jarName;
   }

   public void setJarName(String jarName)
   {
      this.jarName = jarName;
   }

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }
}
