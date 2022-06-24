package com.example.chatbox2.channelcreate

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox2.R
import com.example.chatbox2.channel.ChannelActivity
import com.example.chatbox2.databinding.ActivityChannelCreateBinding
import com.sendbird.android.GroupChannel
import com.sendbird.android.GroupChannelParams
import com.sendbird.android.SendBird
import com.sendbird.android.User

class ChannelCreateActivity : AppCompatActivity(), ChannelCreateAdapter.OnItemCheckedChangeListener {

    private lateinit var channelCreateBinding: ActivityChannelCreateBinding

    private val EXTRA_CHANNEL_URL = "EXTRA_CHANNEL_URL"

    private lateinit var selectedUsers: ArrayList<String>
    private lateinit var adapter: ChannelCreateAdapter
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        channelCreateBinding = ActivityChannelCreateBinding.inflate(layoutInflater)
        setContentView(channelCreateBinding.root)

        selectedUsers = ArrayList()

        adapter = ChannelCreateAdapter(this)
        recyclerView = channelCreateBinding.recyclerCreate
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadUsers()
        channelCreateBinding.buttonCreate.setOnClickListener { createChannel(selectedUsers)}

    }

    private fun createChannel(users: MutableList<String>) {
        val params = GroupChannelParams()

        val operatorId = ArrayList<String>()
        operatorId.add(SendBird.getCurrentUser().userId)

        params.addUserIds(users)
        params.setOperatorUserIds(operatorId)

        GroupChannel.createChannel(params) { groupChannel, e ->
            if (e != null) {
                Log.e("TAG", e.message.toString())
            } else {
                val intent = Intent(this, ChannelActivity::class.java)
                intent.putExtra(EXTRA_CHANNEL_URL, groupChannel.url)
                startActivity(intent)
            }
        }
    }

    override fun onItemChecked(user: User, checked: Boolean) {
        if (checked) {
            selectedUsers.add(user.userId)
        } else {
            selectedUsers.remove(user.userId)
        }
    }

    private fun loadUsers() {
        val userListQuery = SendBird.createApplicationUserListQuery()

        userListQuery.next { list, e ->
            if (e != null) {
                e.message?.let { Log.e("TAG", it) }
            } else {
                adapter.addUsers(list)
            }
        }
    }
}