package me.xuan.speaker

import android.media.AudioManager
import android.util.Log
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode

/**
 * Author: xuan
 * Created on 2021/8/13 10:19.
 *
 * Describe:
 */
object SpeakManager {

    const val isLog: Boolean = false
    const val TAG = "Baidu_Speak"

    const val BD_SPEAK_APP_ID = "BD_SPEAK_APP_ID"
    const val BD_SPEAK_APP_KEY = "BD_SPEAK_APP_KEY"
    const val BD_SPEAK_SECRET_KEY = "BD_SPEAK_SECRET_KEY"

    // ===============初始化参数设置完毕，更多合成参数请至getParams()方法中设置 =================
    var mSpeechSynthesizer: SpeechSynthesizer? = null

    fun initBaiduTTS(): SpeechSynthesizer? {
        val appId = Utils.readMetaDataIntFromApplication(BD_SPEAK_APP_ID)
        val appKey = Utils.readMetaDataFromApplication(BD_SPEAK_APP_KEY)
        val secretKey = Utils.readMetaDataFromApplication(BD_SPEAK_SECRET_KEY)
        // TtsMode.MIX; 离在线融合，在线优先； TtsMode.ONLINE 纯在线； 没有纯离线
        val ttsMode: TtsMode = TtsMode.ONLINE
        return initTTs(appId ?: "", appKey ?: "", secretKey ?: "", ttsMode)
    }

    private fun initTTs(
        appId: String,
        appKey: String,
        secretKey: String,
        ttsMode: TtsMode
    ): SpeechSynthesizer? {
        // 1. 获取实例
        val mSpeechSynthesizer = SpeechSynthesizer.getInstance()
        mSpeechSynthesizer.setContext(Utils.get())

        // 2. 设置listener
        mSpeechSynthesizer.setSpeechSynthesizerListener(object : SpeechSynthesizerListener {
            override fun onSynthesizeStart(p0: String?) {
                toprint("onSynthesizeStart : " + p0)
            }

            override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int, p3: Int) {
                toprint("onSynthesizeDataArrived : " + p0)
            }

            override fun onSynthesizeFinish(p0: String?) {
                toprint("onSynthesizeFinish : " + p0)
            }

            override fun onSpeechStart(p0: String?) {
                toprint("语音开始合成")
            }

            override fun onSpeechProgressChanged(p0: String?, p1: Int) {
                toprint("onSpeechProgressChanged : " + p0)
            }

            override fun onSpeechFinish(p0: String?) {
                toprint("语音合成完成")
            }

            override fun onError(p0: String?, p1: SpeechError?) {
                toprint("onError : " + p0 + ", error : " + p1.toString())
            }

        })

        // 3. 设置appId，appKey.secretKey
        toprint("appId: " + appId)
        mSpeechSynthesizer.setAppId(appId)
        toprint("appKey: " + appKey + ", secretKey: " + secretKey)
        mSpeechSynthesizer.setApiKey(appKey, secretKey)

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0")
        // 设置合成的音量，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9")
        // 设置合成的语速，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5.5")
        // 设置合成的语调，0-15 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "6")

//         mSpeechSynthesizer.setAudioStreamType(AudioManager.MODE_IN_CALL); // 调整音频输出

        // 6. 初始化
        val result = mSpeechSynthesizer?.initTts(ttsMode)
        if (result == 0) {
            toprint("语音合成初始化成功")
        } else {
            checkResult(result ?: -1, "initTts")
        }

        return mSpeechSynthesizer
    }

    override fun equals(other: Any?): Boolean {
        return super.equals(other)
    }

    fun speak(content: String) {

        if (mSpeechSynthesizer == null) {
            mSpeechSynthesizer = initBaiduTTS()
        }
        val result = mSpeechSynthesizer?.speak(content)
        toprint("合成并播放 按钮已经点击")
        checkResult(result ?: -1, "speak")
    }

    fun onDestroy() {
        if (mSpeechSynthesizer != null) {
            mSpeechSynthesizer?.stop()
            mSpeechSynthesizer?.release()
            mSpeechSynthesizer = null
            toprint("释放资源成功")
        }
    }

    fun checkResult(result: Int, method: String) {
        if (result != 0) {
            toprint("error code :$result method:$method")
        }
    }

    private fun toprint(msg: String) {
        if (isLog) {
            Log.d(TAG, msg)
        }
    }
}