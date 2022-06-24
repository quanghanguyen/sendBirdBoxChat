package com.example.chatbox2.channellist

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox2.R
import com.example.chatbox2.channel.ChannelActivity
import com.example.chatbox2.channelcreate.ChannelCreateActivity
import com.example.chatbox2.databinding.ActivityChannelListBinding
import com.example.chatbox2.databinding.ActivityLoginBinding
import com.sendbird.android.GroupChannel

class ChannelListActivity : AppCompatActivity(), ChannelListAdapter.OnChannelClickedListener {

    private lateinit var channelBinding: ActivityChannelListBinding

    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"

    private lateinit var recyclerView: RecyclerView

    private lateinit var adapter: ChannelListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelBinding = ActivityChannelListBinding.inflate(layoutInflater)
        setContentView(channelBinding.root)

        adapter = ChannelListAdapter(this)
        recyclerView = channelBinding.recyclerGroupChannels
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        channelBinding.fabGroupChannelCreate.setOnClickListener{
            val intent = Intent(this, ChannelCreateActivity::class.java)
            startActivity(intent)
        }

        addChannels()
    }

    private fun addChannels() {
        val channelList = GroupChannel.createMyGroupChannelListQuery()
        channelList.limit = 100
        channelList.next { list, e ->
            if (e != null) {
                e.message?.let { Log.e("TAG", it) }
            }
            adapter.addChannels(list)
        }
    }

    override fun onItemClicked(channel: GroupChannel) {
        val intent = Intent(this, ChannelActivity::class.java)
        intent.putExtra(EXTRA_CHANNEL_URL, channel.url)
        startActivity(intent)
    }
}