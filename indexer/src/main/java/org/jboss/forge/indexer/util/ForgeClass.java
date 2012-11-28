package org.jboss.forge.indexer.util;

import java.io.Serializable;

public class ForgeClass implements Serializable
{

   private static final long serialVersionUID = 1L;
   private Long id;
   private String className;
   private String packageName;
   private String jarName;

   public ForgeClass()
   {
      // TODO Auto-generated constructor stub
   }

   public ForgeClass(Long id, String className, String packageName, String jarName)
   {
      this.id = id;
      this.className = className;
      this.packageName = packageName;
      this.jarName = jarName;
   }

   public Long getId()
   {
      return id;
   }

   public void setId(Long id)
   {
      this.id = id;
   }

   public String getClassName()
   {
      return className;
   }

   public void setClassName(String className)
   {
      this.className = className;
   }

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   public String getJarName()
   {
      return jarName;
   }

   public void setJarName(String jarName)
   {
      this.jarName = jarName;
   }
}
