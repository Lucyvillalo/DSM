package com.example.analisis_resultadosguia9

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.analisis_resultadosguia9.databinding.ItemRepositoryBinding

class RepositoryAdapter(private val open: (GitHubRepository) -> Unit) :
    RecyclerView.Adapter<RepositoryAdapter.Holder>() {
    private val items = mutableListOf<GitHubRepository>()
    fun clear() { val count = items.size; items.clear(); notifyItemRangeRemoved(0, count) }
    fun append(repositories: List<GitHubRepository>) {
        val start = items.size; items.addAll(repositories); notifyItemRangeInserted(start, repositories.size)
    }
    override fun getItemCount() = items.size
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        Holder(ItemRepositoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    override fun onBindViewHolder(holder: Holder, position: Int) = holder.bind(items[position])
    inner class Holder(private val binding: ItemRepositoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: GitHubRepository) {
            binding.repoName.text = repo.name
            binding.repoDescription.text = repo.description ?: "Sin descripción"
            binding.repoDetails.text = "${repo.language ?: "Sin lenguaje"}  •  ★ ${repo.stars}"
            binding.root.setOnClickListener { open(repo) }
        }
    }
}
