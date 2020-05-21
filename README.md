GitHub Stars
============
This is just a sample app, created to serve as a demonstration of coding
skills when I am going through job interviews. In this codebase, I use:  
* [Apollo](https://github.com/apollographql/apollo-android)
* The [GitHub GraphQL API](https://developer.github.com/v4/)
* RecyclerViews (with empty views and a decoration that only appears
between rows, not after the last row)
* The
[Navigation component](https://developer.android.com/guide/navigation?gclid=Cj0KCQjwzZj2BRDVARIsABs3l9IiuzW3Rt2pKwVJFp1Ejy5178K6VB-Fu8mh1Z3UA919exKydeD6TcsaAjTqEALw_wcB&gclsrc=aw.ds)
from [Android Jetpack](https://developer.android.com/jetpack)
* [Chrome Custom Tabs](https://developers.google.com/web/android/custom-tabs/implementation-guide)
(which requires a special integration to work with Navigation)
* Encryption to make the access token more difficult to retrieve from the APK
* The [MVP](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93presenter)
architectural pattern, following
[Google's architecture recommendations](https://developer.android.com/jetpack/docs/guide)

It may also be beneficial for other developers to refer to this code when
they need similar functionality, especially as it is thoroughly commented.
Although this app is production ready, it is not intended for use by end users,
as they would find little value in it. All the app does, from an end user's
perspective, is to search GitHub organizations by name and show the top
three repositories for that organization by how many times they have been
starred.

Getting Started
---------------

This project requires a personal access token for the GitHub API.
1. Clone this repo, or otherwise install on your development machine.
2. Follow the instructions from GitHub for
[Creating a personal access token for the command line](https://help.github.com/en/github/authenticating-to-github/creating-a-personal-access-token-for-the-command-line)
with the `read:org` permission only.
3. Create a `sensitive-values.properties` file in the root of the `app`
module (the same folder that `sensitive-values.gradle` in).
4. Add a line setting `GITHUB_ACCESS_TOKEN` to that personal access
token, like so: `GITHUB_ACCESS_TOKEN=abc123de45f6ghi789`
5. Recommended: add `sensitive-values.properties` to your `.gitignore`.
(This is only strictly necessary if you will be pushing to a public repository,
but it doesn't hurt in other cases.)
6. Build and run the app.
