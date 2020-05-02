package com.example.githubstars.githubrepolist

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.githubstars.R
import com.example.githubstars.data.GitHubRepo
import com.example.githubstars.databinding.ListItemGithubRepoBinding
import com.example.githubstars.util.toCompactString

class GitHubRepoAdapter : ListAdapter<GitHubRepo, GitHubRepoAdapter.GitHubRepoViewHolder>(
    GitHubRepoDiffCallback()
) {
    //TODO: should the click handling be outside the adapter?
    // TODO: can I use view binding with view holders?
    class GitHubRepoViewHolder(private val binding: ListItemGithubRepoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(repo: GitHubRepo) {
            binding.root.setOnClickListener {
                //TODO: progress indicator for chrome custom tabs - maybe Rx to prevent double taps?
                //TODO temp
//                val action = GitHubRepoListFragmentDirections.actionGitHubRepoListFragmentToBlankFragment()
//                binding.root.findNavController().navigate(action)
                //TODO: is this the best way to do this?
                // Using Chrome Custom Tabs instead of a WebView. This is Google's recommendation in most
                // cases, per https://developer.chrome.com/multidevice/android/customtabs#whentouse
                // admittedly, it has trade-offs, particularly when using Navigation Components and
                // custom transitions, so it required some extra work. Still, it's a more powerful
                // long term solution. The alternative would have been to make a Fragment for the detail
                // view, with its own app bar, and then to run a shared element transition through
                // Navigation Component Animations.
                if (repo.url != null) {
                    //TODO: temp
                    Log.e("xxx", "Clicked to open ${repo.url}")

                    val action =
                        GitHubRepoListFragmentDirections.actionGitHubRepoListFragmentToGithubRepoDetail(
                            repo.url
                        )
                    binding.root.findNavController().navigate(action)
                } else {
                    //TODO: test
                    Log.w(TAG, "User clicked the row for this repo, but there was no url: $repo")
                }

            }
            Log.d("adapter", "setting binding with repo name ${repo.name}")
            binding.repoNameTextView.text = repo.name
            binding.repoDescriptionTextView.text = repo.description

            // Suppose we want to show a repository that is starred a great many times, like the Retrofit repo
            // from Square with, at the moment, 35372 stars. On Nougat and above we can use a more compact format,
            // like "35k". Below Nougat, we show "35372". We can let it take more screen width, manually format it,
            // or shrink the text size. In list_item_github_repo.xml I'm using TextView auto sizing to shrink it.
            // I could write code for older APIs to format it, but then that would require more work and testing,
            // and then two separate sections of code to format the number would need to be maintained, all
            // for the sake of older devices. Better to do something simple on older devices until those users upgrade.
            // In a business environment though, designers and product managers might decide it would be worth it.
            binding.repoStarCountTextView.text = repo.starCount.toCompactString()
        }
    }

   //TODO: figure out logging/debug strategy

    //TODO: comment on how I considered using cards like the GitHub cards in "pinned repositories" but wanted to follow Android patterns and stick to the NYTimes stark, monochrome style

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GitHubRepoViewHolder {
        //TODO: use binding.inflate() argument with parent parameter
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_github_repo, parent, false)
        val binding = ListItemGithubRepoBinding.bind(view)
        //TODO: did this cause the problems?
//        val binding = ListItemGithubRepoBinding.inflate(LayoutInflater.from(parent.context))
        return GitHubRepoViewHolder(
            binding
        )
    }

//    override fun getItemCount(): Int {
//        return items.size
//    }

//    private fun getItem(index: Int): GitHubRepo? {
//        var item: GitHubRepo? = null
//        if (index >= 0 && index <= items.size - 1) {
//            item = items[index]
//        }
//        return item
//    }

    override fun onBindViewHolder(holder: GitHubRepoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object {
        const val TAG = "GitHubRepoAdapter"
    }
}

private class GitHubRepoDiffCallback : DiffUtil.ItemCallback<GitHubRepo>() {
    override fun areItemsTheSame(oldItem: GitHubRepo, newItem: GitHubRepo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GitHubRepo, newItem: GitHubRepo): Boolean {
        return oldItem.name == newItem.name
                && oldItem.description == newItem.description
                && oldItem.starCount == newItem.starCount
    }
}
