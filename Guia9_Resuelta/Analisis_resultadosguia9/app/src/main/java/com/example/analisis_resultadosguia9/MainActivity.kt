package com.example.analisis_resultadosguia9

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.analisis_resultadosguia9.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RepositoryAdapter
    private var activeCall: Call<List<GitHubRepository>>? = null
    private var username = ""
    private var nextPage = 1
    private var hasMore = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.ime())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        adapter = RepositoryAdapter { repo ->
            try { startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(repo.htmlUrl))) }
            catch (_: android.content.ActivityNotFoundException) {
                Toast.makeText(this, "No hay un navegador disponible", Toast.LENGTH_SHORT).show()
            }
        }
        binding.repositories.layoutManager = LinearLayoutManager(this)
        binding.repositories.adapter = adapter
        binding.searchUsers.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String?) = true
            override fun onQueryTextSubmit(query: String?): Boolean {
                search(query.orEmpty().trim())
                return true
            }
        })
        binding.loadMore.setOnClickListener { loadPage() }
        savedInstanceState?.getString("username")?.takeIf { it.isNotBlank() }?.let {
            binding.searchUsers.setQuery(it, false)
            search(it)
        }
    }

    private fun search(value: String) {
        activeCall?.cancel()
        activeCall = null
        adapter.clear()
        hasMore = false
        username = ""
        showLoading(false)
        if (!Regex("[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?").matches(value)) {
            binding.status.text = "Ingresa un usuario válido de GitHub (sin espacios ni @)."
            return
        }
        username = value
        nextPage = 1
        binding.searchUsers.clearFocus()
        loadPage()
    }

    private fun showLoading(loading: Boolean) {
        binding.progress.visibility = if (loading) View.VISIBLE else View.GONE
        binding.loadMore.visibility = if (hasMore && !loading) View.VISIBLE else View.GONE
    }

    private fun loadPage() {
        showLoading(true)
        binding.status.text = "Buscando repositorios de $username…"
        val call = RetrofitClient.api.repositories(username, nextPage)
        activeCall = call
        call.enqueue(object : Callback<List<GitHubRepository>> {
            override fun onResponse(call: Call<List<GitHubRepository>>, response: Response<List<GitHubRepository>>) {
                if (activeCall !== call) return
                activeCall = null
                val repos = response.body()
                if (response.isSuccessful && repos != null) {
                    adapter.append(repos)
                    nextPage++
                    hasMore = response.headers()["Link"]?.contains("rel=\"next\"") == true
                    binding.status.text = if (adapter.itemCount == 0) "$username no tiene repositorios públicos."
                        else "${adapter.itemCount} repositorios de $username. Toca uno para abrirlo."
                } else {
                    binding.status.text = when (response.code()) {
                        404 -> "No se encontró el usuario $username."
                        403, 429 -> "GitHub ha limitado las consultas. Inténtalo más tarde."
                        else -> "No se pudieron obtener los repositorios (HTTP ${response.code()}). Inténtalo de nuevo."
                    }
                }
                showLoading(false)
            }
            override fun onFailure(call: Call<List<GitHubRepository>>, t: Throwable) {
                if (activeCall !== call || call.isCanceled) return
                activeCall = null
                binding.status.text = "No se pudo conectar con GitHub. Revisa tu conexión y vuelve a buscar."
                showLoading(false)
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("username", username)
        super.onSaveInstanceState(outState)
    }
    override fun onDestroy() {
        activeCall?.cancel()
        activeCall = null
        super.onDestroy()
    }
}
