package com.example.chatbox2.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.chatbox2.channellist.ChannelListActivity
import com.example.chatbox2.databinding.ActivityLoginBinding
import com.sendbird.android.SendBird

class LoginActivity : AppCompatActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private val appID = "5C644291-0831-49F0-AB74-5945D49D6004"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(loginBinding.root)

        SendBird.init(appID, this)
        initEvents()
    }

    private fun initEvents() {
        loginClick()
    }

    private fun loginClick() {
        loginBinding.buttonLoginConnect.setOnClickListener {
            connectToSendBird(loginBinding.edittextLoginUserId.text.toString(), 
                loginBinding.edittextLoginNickname.text.toString())
        }
    }

    private fun connectToSendBird(userID: String, nickname: String) {
        SendBird.connect(userID) { user, e ->
            if (e != null) {
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            } else {
                SendBird.updateCurrentUserInfo(nickname, null) { e ->
                    if (e != null) {
                        Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                    }
                    val intent = Intent(this, ChannelListActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}