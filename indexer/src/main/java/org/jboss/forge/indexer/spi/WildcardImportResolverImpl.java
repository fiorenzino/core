package org.jboss.forge.indexer.spi;

import java.util.List;
import java.util.ServiceLoader;

import org.jboss.forge.parser.java.JavaSource;
import org.jboss.forge.parser.spi.IndexRepository;
import org.jboss.forge.parser.spi.WildcardImportResolver;

public class WildcardImportResolverImpl implements WildcardImportResolver
{

   public static ServiceLoader<IndexRepository> loader = ServiceLoader
            .load(IndexRepository.class);
   private static List<IndexRepository> indexers;

   @Override
   public String resolve(JavaSource<?> source, String type)
   {
      // TODO Auto-generated method stub
      return null;
   }

}
