<html>
<body>
	<h2>${application}</h2>

	Memory Info:<br />
	<img src="?type=graph&app=${app!''}&key=MemoryInfoCollector&gname=memory.used.heap">
	<img src="?type=graph&app=${app!''}&key=MemoryInfoCollector&gname=memory.used.Eden">
	<img src="?type=graph&app=${app!''}&key=MemoryInfoCollector&gname=memory.used.heap.OldGen">
	<img src="?type=graph&app=${app!''}&key=MemoryInfoCollector&gname=memory.used.NonHeap">
	<img src="?type=graph&app=${app!''}&key=MemoryInfoCollector&gname=memory.used.PermGen">
	<img src="?type=graph&app=${app!''}&key=MemoryInfoCollector&gname=memory.used.Survivor"><br />
	GC Info:<br />
	<img src="?type=graph&app=${app!''}&key=GarbageCollectorInfoCollector&gname=PS%20MarkSweep%20gc_average_pause_time">
	<img src="?type=graph&app=${app!''}&key=GarbageCollectorInfoCollector&gname=PS%20MarkSweep%20gc_throughputs">
	<img src="?type=graph&app=${app!''}&key=GarbageCollectorInfoCollector&gname=PS%20Scavenge%20gc_average_pause_time">
	<img src="?type=graph&app=${app!''}&key=GarbageCollectorInfoCollector&gname=PS%20Scavenge%20gc_throughputs"><br />
	JVM Info:<br />
	<img src="?type=graph&app=${app!''}&key=JavaVirtualMachineInfoCollector&gname=jvm.cpu">
	<img src="?type=graph&app=${app!''}&key=JavaVirtualMachineInfoCollector&gname=jvm.averageSystemLoad">
	<img src="?type=graph&app=${app!''}&key=JavaVirtualMachineInfoCollector&gname=jvm.openFileDescriptorCount">
	<img src="?type=graph&app=${app!''}&key=JavaVirtualMachineInfoCollector&gname=jvm.threadCount">
</body>
</html>