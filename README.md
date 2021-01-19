
##To profile:
get asyncprofiler https://github.com/jvm-profiling-tools/async-profiler   


set env path with
LD_LIBRARY_PATH=/....../async-profiler-1.8.2-linux-x64/build  


sudo -s
apt install openjdk-8-dbg  

echo 1 > /proc/sys/kernel/perf_event_paranoid
echo 0 > /proc/sys/kernel/kptr_restrict
