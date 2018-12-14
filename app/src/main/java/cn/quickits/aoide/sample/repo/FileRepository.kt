package cn.quickits.aoide.sample.repo

import cn.quickits.aoide.sample.repo.bean.RecordFile
import cn.quickits.aoide.sample.util.DirUtils
import cn.quickits.aoide.sample.util.GlobalVars
import com.blankj.utilcode.util.FileUtils
import io.reactivex.Flowable


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:02
 **/
class FileRepository {

    fun loadRecordFile(): Flowable<List<RecordFile>> {
        return Flowable.just(Any())
            .map {
                val result = arrayListOf<RecordFile>()

                val files = FileUtils.listFilesInDir(DirUtils.cacheDirPath)

                for (file in files) {
                    if (file.absolutePath == GlobalVars.currentRecordingFile) continue

                    result.add(
                        RecordFile(
                            FileUtils.getFileName(file),
                            file.absolutePath
                        )
                    )
                }

                if (result.isEmpty()) throw RuntimeException("暂无录音文件")

                result.sortByDescending { item -> item.fileName }

                return@map result
            }
    }

    fun deleteRecordFile(recordFile: RecordFile): Flowable<Boolean> {
        return Flowable.just(recordFile)
            .map { FileUtils.delete(it.filePath) }
    }

    companion object {

        @Volatile
        private var INSTANCE: FileRepository? = null

        fun getInstance(): FileRepository {
            if (INSTANCE == null) {
                synchronized(FileRepository::class) {
                    if (INSTANCE == null) {
                        INSTANCE = FileRepository()
                    }
                }
            }
            return INSTANCE as FileRepository
        }
    }
}