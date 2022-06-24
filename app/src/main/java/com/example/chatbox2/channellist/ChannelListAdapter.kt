package com.example.chatbox2.channellist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox2.databinding.ItemChannelChooserBinding
import com.sendbird.android.AdminMessage
import com.sendbird.android.FileMessage
import com.sendbird.android.GroupChannel
import com.sendbird.android.UserMessage

class ChannelListAdapter(listener: OnChannelClickedListener) : RecyclerView.Adapter<ChannelListAdapter.ChannelHolder>() {

    interface OnChannelClickedListener {
        fun onItemClicked(channel: GroupChannel)
    }

    class ChannelHolder(private val listBinding : ItemChannelChooserBinding) : RecyclerView.ViewHolder(listBinding.root) {
        private val channelName = listBinding.textChannelName
        private val channelRecentMessage = listBinding.textChannelRecent
        private val channelMemberCount = listBinding.textChannelMemberCount

        fun bindViews(groupChannel: GroupChannel, listener: OnChannelClickedListener) {
            val lastMessage = groupChannel.lastMessage

            if (lastMessage != null) {

                when (lastMessage) {
                    is UserMessage -> channelRecentMessage.setText(lastMessage.message)
                    is AdminMessage -> channelRecentMessage.setText(lastMessage.message)
                    else -> {
                        val fileMessage = lastMessage as FileMessage
                        val sender = fileMessage.sender.nickname

                        channelRecentMessage.text = sender
                    }
                }
            }

            itemView.setOnClickListener {
                listener.onItemClicked(groupChannel)
            }

            channelName.text = groupChannel.members[0].nickname
            channelMemberCount.text = groupChannel.memberCount.toString()
        }

    }

    private val listener: OnChannelClickedListener
    private var channels: MutableList<GroupChannel>

    init {
        channels = ArrayList()
        this.listener = listener
    }

    fun addChannels(channels: MutableList<GroupChannel>) {
        this.channels = channels
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelHolder {
        val layoutInflater =
            ItemChannelChooserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ChannelHolder(layoutInflater.inflate(R.layout.item_channel_chooser, parent, false))
        return ChannelHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: ChannelHolder, position: Int) {
        holder.bindViews(channels[position], listener)
    }

    override fun getItemCount(): Int {
        return channels.size
    }

}