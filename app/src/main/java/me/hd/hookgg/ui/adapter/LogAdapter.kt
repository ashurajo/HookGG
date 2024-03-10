package me.hd.hookgg.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.hd.hookgg.databinding.ItemRvLogBinding
import java.io.FileOutputStream

class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    private var logList = mutableListOf<CharSequence>()

    fun outLog(outputStream: FileOutputStream) {
        logList.forEach {
            outputStream.write(it.toString().toByteArray())
            outputStream.write("\n".toByteArray())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun clearLog() {
        logList.clear()
        notifyDataSetChanged()
    }

    fun addLog(log: CharSequence) {
        logList.add(log)
        notifyItemInserted(logList.size - 1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val binding = ItemRvLogBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return LogViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.tvLog.text = logList[position]
    }

    override fun getItemCount(): Int = logList.size

    class LogViewHolder(binding: ItemRvLogBinding) : RecyclerView.ViewHolder(binding.root) {
        val tvLog = binding.itemRvTvLog
    }
}