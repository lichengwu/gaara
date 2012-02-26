<%@page import="java.util.concurrent.TimeUnit"%>
<%@page import="com.meituan.gaara.jvm.memory.MemoryInfo"%>
<html>
<body>
<h2>Hello World!</h2>
<%MemoryInfo in = MemoryInfo.getInstance();
in.refresh();
//while(true){
out.print(in.toString());
//TimeUnit.MILLISECONDS.sleep(500);
//}
%>
</body>
</html>
