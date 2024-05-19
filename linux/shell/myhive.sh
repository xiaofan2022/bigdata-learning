#!/bin/bash
# 一键启动、停止、查看Hive的metastore和hiveserver2两个服务的脚本
HIVE_LOG_DIR=/opt/software/hive-2.3.9/logs
function start_metastore {
  # 启动Hive metastore服务
  hive --service metastore >$HIVE_LOG_DIR/hiveServer2.log 2>&1 &
  for i in {1..30}; do
    if is_metastore_running; then
      echo "Hive metastore服务已经成功启动！"
      return 0
    else
      sleep 1 # 等待1秒
    fi
  done
  echo "Hive metastore服务启动失败，请查看日志！"
  return 1
}
function stop_metastore {
  # 停止Hive metastore服务
  ps -ef | grep hive.metastore | grep -v grep | awk '{print $2}' | xargs -r kill -9 >/dev/null 2>&1
  if is_metastore_running; then
    echo "Hive metastore服务停止失败，请检查日志！"
    return 1
  else
    echo "Hive metastore服务已经成功停止！"
    return 0
  fi
}
function start_hiveserver2 {
  # 启动HiveServer2服务
  hive --service hiveserver2 >$HIVE_LOG_DIR/hiveServer2.log 2>&1 &
  for i in {1..30}; do
    if is_hiveserver2_running; then
      echo "HiveServer2服务已经成功启动！"
      return 0
    else
      sleep 1 # 等待1秒
    fi
  done
  echo "HiveServer2服务启动失败，请查看日志！"
  return 1
}
function stop_hiveserver2 {
  # 停止HiveServer2服务
  ps -ef | grep hiveserver2 | grep -v grep | awk '{print $2}' | xargs -r kill -9 >/dev/null 2>&1
  if is_hiveserver2_running; then
    echo "HiveServer2服务停止失败，请检查日志！"
    return 1
  else
    echo "HiveServer2服务已经成功停止！"
    return 0
  fi
}
function is_metastore_running {
  # 检查Hive metastore服务是否在运行
  ps -ef | grep hive.metastore | grep -v grep >/dev/null 2>&1
}
function is_hiveserver2_running {
  # 检查HiveServer2服务是否在运行
  ps -ef | grep hiveserver2 | grep -v grep >/dev/null 2>&1
}
# 检查参数
if [ "$1" = "start" ]; then
  if start_metastore && start_hiveserver2; then
    exit 0
  else
    exit 1
  fi
elif [ "$1" = "stop" ]; then
  if stop_hiveserver2 && stop_metastore; then
    exit 0
  else
    exit 1
  fi
elif [ "$1" = "status" ]; then
  if is_metastore_running; then
    echo "Hive metastore服务正在运行！"
  else
    echo "Hive metastore服务未运行！"
  fi
  if is_hiveserver2_running; then
    echo "HiveServer2服务正在运行！"
  else
    echo "HiveServer2服务未运行！"
  fi
else
  echo "Usage: $0 [start|stop|status]"
  exit 1
fi