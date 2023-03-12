mvn exec:java -Dexec.mainClass="org.qcri.Main" -Dexec.args="/disk/com.qcri.hackit 127.0.0.1 /disk/com.qcri.hackit/src/main/resources/oneline.txt /disk/com.qcri.hackit/output hakespeare debug"



mvn exec:java -Dexec.mainClass="org.qcri.Main" -Dexec.args="/disk/com.qcri.hackit 10.4.4.32 hdfs://10.4.4.32:8300/data/long_abstract_clean/1GB hdfs://10.4.4.32:8300/output hakespeare debug" -Dexec.cleanupDaemonThreads=false