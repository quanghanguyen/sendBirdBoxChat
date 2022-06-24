package com.example.chatbox2.channelcreate

import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.chatbox2.databinding.ItemCreateBinding
import com.sendbird.android.User

class ChannelCreateAdapter( listener: OnItemCheckedChangeListener) : RecyclerView.Adapter<ChannelCreateAdapter.UserHolder>() {

    interface OnItemCheckedChangeListener {
        fun onItemChecked(user: User, checked: Boolean)
    }

    private var users: MutableList<User>
    private var checkedListener: OnItemCheckedChangeListener

    companion object {
        fun selectedUsers() = ArrayList<String>()
        fun sparseArray() = SparseBooleanArray()
    }

    init {
        users = ArrayList()
        this.checkedListener = listener
    }

    fun addUsers(users: MutableList<User>) {
        this.users = users
        notifyDataSetChanged()
    }

    class UserHolder(private val itemCreateBinding: ItemCreateBinding) : RecyclerView.ViewHolder(itemCreateBinding.root) {
        private val checkbox = itemCreateBinding.checkboxCreate
        private val userId = itemCreateBinding.textCreateUser

        fun bindViews(user: User, position: Int, listener: OnItemCheckedChangeListener) {

            userId.text = user.userId

            checkbox.isChecked = sparseArray().get(position, false)

            checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                listener.onItemChecked(user, isChecked)

                if (isChecked) {
                    selectedUsers().add(user.userId)
                    sparseArray().put(position, true)
                } else {
                    selectedUsers().remove(user.userId)
                    sparseArray().put(position, false)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserHolder {
        val layoutInflater = ItemCreateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserHolder(layoutInflater)
    }

    override fun onBindViewHolder(holder: UserHolder, position: Int) {
        holder.bindViews(users[position], position, checkedListener)
    }

    override fun getItemCount(): Int {
        return users.size
    }
}