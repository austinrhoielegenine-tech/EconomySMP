package org.apache.commons.collections4.trie;

import java.util.Map;
import org.apache.commons.collections4.trie.analyzer.StringKeyAnalyzer;

public class PatriciaTrie<V> extends AbstractPatriciaTrie<String, V> {
   private static final long serialVersionUID = 4446367780901817838L;

   public PatriciaTrie() {
      super(StringKeyAnalyzer.INSTANCE);
   }

   public PatriciaTrie(Map<? extends String, ? extends V> map) {
      super(StringKeyAnalyzer.INSTANCE, map);
   }
}
