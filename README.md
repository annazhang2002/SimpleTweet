# Project 3 - *SimpleTweet*

**SimpleTweet** is an android app that allows a user to view their Twitter timeline and post a new tweet. The app utilizes [Twitter REST API](https://dev.twitter.com/rest/public).

Time spent: **X** hours spent in total

## User Stories

The following **required** functionality is completed:

* [X]	User can **sign in to Twitter** using OAuth login
* [X]	User can **view tweets from their home timeline**
  * [X] User is displayed the username, name, and body for each tweet
  * [X] User is displayed the [relative timestamp](https://gist.github.com/nesquena/f786232f5ef72f6e10a7) for each tweet "8m", "7h"
* [X] User can **compose and post a new tweet**
  * [X] User can click a “Compose” icon in the Action Bar on the top right
  * [X] User can then enter a new tweet and post this to twitter
  * [X] User is taken back to home timeline with **new tweet visible** in timeline
  * [X] Newly created tweet should be manually inserted into the timeline and not rely on a full refresh
* [X] User can **see a counter with total number of characters left for tweet** on compose tweet page
* [X] User can **pull down to refresh tweets timeline**
* [X] User can **see embedded image media within a tweet** on list or detail view.

The following **stretch** features are implemented:

* [X] User is using **"Twitter branded" colors and styles**
    * [X] Added border between different tweets as well as padding
* [X] User sees an **indeterminate progress indicator** when any background or network task is happening
* [X] User can **select "reply" from detail view to respond to a tweet**
  * [X] User that wrote the original tweet is **automatically "@" replied in compose**
* [X] User can tap a tweet to **open a detailed tweet view**
  * [X] User can **take favorite (and unfavorite) or reweet** actions on a tweet
* [X] User can view more tweets as they scroll with infinite pagination
* [X] Compose tweet functionality is build using modal overlay
* [X] User can **click a link within a tweet body** on tweet details view. The click will launch the web browser with relevant page opened.
* [X] Use Parcelable instead of Serializable using the popular [Parceler library](http://guides.codepath.org/android/Using-Parceler).
* [X] Replace all icon drawables and other static image assets with [vector drawables](http://guides.codepath.org/android/Drawables#vector-drawables) where appropriate.
* [X] User can view following / followers list through any profile they view.
* [X] Use the View Binding library to reduce view boilerplate.
* [ ] On the Twitter timeline, leverage the [CoordinatorLayout](http://guides.codepath.org/android/Handling-Scrolls-with-CoordinatorLayout#responding-to-scroll-events) to apply scrolling behavior that [hides / shows the toolbar](http://guides.codepath.org/android/Using-the-App-ToolBar#reacting-to-scroll).
* [X] User can **open the twitter app offline and see last loaded tweets**. Persisted in SQLite tweets are refreshed on every application launch. While "live data" is displayed when app can get it from Twitter API, it is also saved for use in offline mode.

The following **additional** features are implemented:
* [X] User can like/unlike as well as retweet/unretweet from their home timeline
* [X] Embedded media images have rounded corners
* [X] Updated Login screen to better match the design of the rest of the app
* [X] Added adaptive icon for the app that is the twitter logo
* [X] Created user profile pages
    * [X] Can access by tapping user's profile picture on home timeline or details screen
    * [X] Shows the user's bio, name, profile picture, screen name, banner image, location, website url, date joined, follower count, and following count
    * [X] User can click on follower or following count to open FollowersActivity
* [X] From the FollowersActivity, users can scroll through a list of all of the users that user follows/following
* [X] Added verified symbol to home timeline, details screen, user profile, followers list etc.

## Video Walkthroughs

#### Here are a few walkthroughs highlighting various user stories:

Home Timeline of Tweets (Endless Scroll and Links)

![Walkthough](timeline.gif)

Tweet Details (Liking and Retweeting)

![Walkthough](details.gif)

Compose a new tweet and replies

![Walkthough](compose.gif)

User Profiles

![Walkthough](user.gif)

Persistence

![Walkthough](persistence.gif)

Followers and Following

![Walkthough](userLists.gif)

Login

![Walkthough](login.gif)

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Notes

Describe any challenges encountered while building the app.

## Open-source libraries used

- [Android Async HTTP](https://github.com/loopj/android-async-http) - Simple asynchronous HTTP requests with JSON parsing
- [Glide](https://github.com/bumptech/glide) - Image loading and caching library for Android

## License

    Copyright [yyyy] [name of copyright owner]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.