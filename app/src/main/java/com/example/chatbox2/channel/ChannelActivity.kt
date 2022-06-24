package com.example.chatbox2.channel

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox2.channellist.ChannelListActivity
import com.example.chatbox2.databinding.ActivityChatBinding
import com.sendbird.android.*

class ChannelActivity : AppCompatActivity() {

    private lateinit var channelBinding : ActivityChatBinding
    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"
    private val CHANNEL_HANDLER_ID = "CHANNEL_HANDLER_GROUP_CHANNEL_CHAT"

    private lateinit var adapter: MessageAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var groupChannel: GroupChannel
    private lateinit var channelUrl: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelBinding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(channelBinding.root)

        setUpRecyclerView()
        setButtonListeners()
    }

    override fun onResume() {
        super.onResume()
        channelUrl = getChannelURl()

        GroupChannel.getChannel(channelUrl,
            GroupChannel.GroupChannelGetHandler { groupChannel, e ->
                if (e != null) {
                    // Error!
                    e.printStackTrace()
                    return@GroupChannelGetHandler
                }
                this.groupChannel = groupChannel
                getMessages()
            })

        SendBird.addChannelHandler(
            CHANNEL_HANDLER_ID,
            object : SendBird.ChannelHandler() {
                override fun onMessageReceived(
                    baseChannel: BaseChannel,
                    baseMessage: BaseMessage
                ){
                    if (baseChannel.url == channelUrl) {
                        adapter.addFirst(baseMessage)
                        groupChannel.markAsRead()
                    }
                }
            })
    }

    override fun onPause() {
        super.onPause()
        SendBird.removeChannelHandler(CHANNEL_HANDLER_ID)
    }

    private fun setButtonListeners() {
        val back = channelBinding.buttonGchatBack
        back.setOnClickListener {
            val intent = Intent(this, ChannelListActivity::class.java)
            startActivity(intent)
        }

        val send = channelBinding.buttonGchatSend
        send.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage()
    {
        val params = UserMessageParams()
            .setMessage(channelBinding.editGchatMessage.text.toString())
        groupChannel.sendUserMessage(params,
            BaseChannel.SendUserMessageHandler { userMessage, e ->
                if (e != null) {
                    return@SendUserMessageHandler
                }
                adapter.addFirst(userMessage)
                channelBinding.editGchatMessage.text.clear()
            })
    }

    private fun getMessages() {

        val previousMessageListQuery = groupChannel.createPreviousMessageListQuery()

        previousMessageListQuery.load(
            100,
            true,
            object : PreviousMessageListQuery.MessageListQueryResult {
                override fun onResult(
                    messages: MutableList<BaseMessage>?,
                    e: SendBirdException?
                ) {
                    if (e != null) {
                        Log.e("Error", e.message.toString())
                    }
                    adapter.loadMessages(messages!!)
                }
            })

    }

    private fun setUpRecyclerView() {
        adapter = MessageAdapter(this)
        recyclerView = channelBinding.recyclerGchat
        recyclerView.adapter = adapter
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        recyclerView.layoutManager = layoutManager
        recyclerView.scrollToPosition(0)
    }

    private fun getChannelURl(): String {
        val intent = this.intent
        return intent.getStringExtra(EXTRA_CHANNEL_URL).toString()
    }
}