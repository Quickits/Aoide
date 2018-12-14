package cn.quickits.aoide.sample.ui

import cn.quickits.aoide.sample.repo.FileRepository
import cn.quickits.aoide.sample.repo.bean.RecordFile
import cn.quickits.arch.mvvm.QLceViewModel
import com.blankj.utilcode.util.ToastUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers


/**
 * @program: Aoide
 * @description:
 * @author: gavinliu
 * @create: 2018-12-14 12:25
 **/
class RecordFilesViewModel : QLceViewModel<List<RecordFile>>() {

    private val disposables: CompositeDisposable = CompositeDisposable()

    fun load(pullToRefresh: Boolean) {
        displayLoader(pullToRefresh)

        disposables.add(
            FileRepository.getInstance().loadRecordFile()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    content.value = it
                }, {
                    displayError(pullToRefresh, it)
                })
        )
    }

    fun delete(recordFile: RecordFile) {
        disposables.add(
            FileRepository.getInstance().deleteRecordFile(recordFile)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it) load(true)
                }, {
                    ToastUtils.showShort(it.message)
                })
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}