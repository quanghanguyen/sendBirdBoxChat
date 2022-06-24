package com.example.chatbox2.channel

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.chatbox2.R
import com.example.chatbox2.databinding.ItemChatMeBinding
import com.example.chatbox2.databinding.ItemChatOtherBinding
import com.sendbird.android.BaseMessage
import com.sendbird.android.SendBird
import com.sendbird.android.UserMessage
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MessageAdapter(context : Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_USER_MESSAGE_ME = 10
    private val VIEW_TYPE_USER_MESSAGE_OTHER = 11

    private var messages: MutableList<BaseMessage>
    private var context: Context

    init {
        messages = ArrayList()
        this.context = context
    }

    fun loadMessages(messages: MutableList<BaseMessage>) {
        this.messages = messages
        notifyDataSetChanged()
    }

    fun addFirst(message: BaseMessage) {
        messages.add(0, message)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater1 = ItemChatMeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val layoutInflater2 = ItemChatOtherBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when(viewType) {
            VIEW_TYPE_USER_MESSAGE_ME  -> {
                MyUserHolder(layoutInflater1)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER ->  {
                OtherUserHolder(layoutInflater2)
            }
            else -> MyUserHolder(layoutInflater1)
        }
    }

    override fun getItemViewType(position: Int): Int {

        val message = messages.get(position)

        when (message) {
            is UserMessage -> {
                if (message.sender.userId.equals(SendBird.getCurrentUser().userId)) return VIEW_TYPE_USER_MESSAGE_ME
                else return VIEW_TYPE_USER_MESSAGE_OTHER
            }
            else ->  return -1
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_USER_MESSAGE_ME -> {
                holder as MyUserHolder
                holder.bindView(context, messages[position] as UserMessage)
            }
            VIEW_TYPE_USER_MESSAGE_OTHER -> {
                holder as OtherUserHolder
                holder.bindView(context, messages[position] as UserMessage)
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class MyUserHolder(itemChatMeBinding: ItemChatMeBinding) : RecyclerView.ViewHolder(itemChatMeBinding.root) {

        val messageText = itemChatMeBinding.textGchatMessageMe
        val date = itemChatMeBinding.textGchatDateMe
        val messageDate = itemChatMeBinding.textGchatTimestampMe

        fun bindView(context: Context, message: UserMessage) {

            messageText.setText(message.message)
            messageDate.text = DateUtil.formatTime(message.createdAt)

            date.visibility = View.VISIBLE
            date.text = DateUtil.formatDate(message.createdAt)
        }
    }

    class OtherUserHolder(itemChatOtherBinding: ItemChatOtherBinding) : RecyclerView.ViewHolder(itemChatOtherBinding.root) {

        val messageText = itemChatOtherBinding.textGchatMessageOther
        val date = itemChatOtherBinding.textGchatDateOther
        val timestamp = itemChatOtherBinding.textGchatTimestampOther
        val profileImage = itemChatOtherBinding.imageGchatProfileOther
        val user = itemChatOtherBinding.textGchatUserOther

        fun bindView(context: Context, message: UserMessage) {

            messageText.setText(message.message)

            timestamp.text = DateUtil.formatTime(message.createdAt)

            date.visibility = View.VISIBLE
            date.text = DateUtil.formatDate(message.createdAt)

            Glide.with(context).load(message.sender.profileUrl)
                .apply(RequestOptions().override(75, 75))
                .into(profileImage)
            user.text = message.sender.nickname
        }
    }

    object DateUtil {
        fun formatTime(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }

        fun formatDate(timeInMillis: Long): String {
            val dateFormat = SimpleDateFormat("MMMM dd", Locale.getDefault())
            return dateFormat.format(timeInMillis)
        }
    }
}