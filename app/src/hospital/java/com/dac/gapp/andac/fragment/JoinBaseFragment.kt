package com.dac.gapp.andac.fragment

import android.support.v4.app.Fragment
import com.dac.gapp.andac.JoinActivity
import com.dac.gapp.andac.base.BaseFragment
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumFile

abstract class JoinBaseFragment : BaseFragment(){
    abstract fun onChangeFragment()

    fun getJoinActivity() = activity as JoinActivity

    fun startAlbumImage(fragment: Fragment, done : (albumFile : AlbumFile) -> Unit){
        Album.image(fragment)
                .singleChoice()
                .onResult {
                    it.forEach{albumFile ->
                        // uri 저장
                        done(albumFile)
                    }
                }
                .start()
    }
}