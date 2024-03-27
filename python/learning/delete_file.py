import os

def delete_downloading_files(directory):
    for root, dirs, files in os.walk(directory):
        for file in files:
            if file.endswith('.baiduyun.downloading'):
                file_path = os.path.join(root, file)
                try:
                    os.remove(file_path)
                    print(f"Deleted: {file_path}")
                except Exception as e:
                    print(f"Error deleting {file_path}: {str(e)}")

if __name__ == "__main__":
    # 请将要搜索的根目录路径替换为您实际的路径
    root_directory = 'E:\\BaiduNetdiskDownload\\极客'
    delete_downloading_files(root_directory)
