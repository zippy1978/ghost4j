Encoding
========

### Default behavior

The library uses the default JVM encoding, which is inherited from the host OS encoding or specified by the <<file.encoding>> system property.

### Set Ghost4J encoding

However, in some cases (see https://sourceforge.net/projects/ghost4j/forums/forum/886756/topic/4432153) it can be useful to use a specific encoding when dealing with stdin.
To do so, use the **ghost4j.encoding** system property to override the default behavior.

When working with multiple JVMs (see [Thread safety and multi-threading](threadsafetyandmultithreading.html)), this value is also set on the forked JVMs.
  
**Important:** if you consider changing the ghost4j.encoding system property at runtime, make sure to destroy the Ghostscript singleton instance first, otherwise the change will not be applied. To do so call:
  
	Ghostscript.deleteInstance();
