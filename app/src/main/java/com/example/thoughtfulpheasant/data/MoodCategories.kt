package com.example.thoughtfulpheasant.data

import kotlinx.serialization.Serializable

// Data model mapping each slider tier to a category and phrase list
@Serializable
data class MoodCategory(
    val name: String,
    val phrases: List<String>
)

val defaultMoodCategories = listOf(
    MoodCategory(
        name = "Inspire Me",
        phrases = listOf(
            "Life changes when we change",
            "You can't pour from an empty cup. Self-care isn't selfish - it's necessary",
            "You can't find your own path using someone else's map",
            "The best revenge is not to be like your enemy",
            "Do not set yourself on fire to keep others warm",
            "If you don't like where you are, move, you are not a tree",
        )
    ),
    MoodCategory(
        name = "Thoughtful",
        phrases = listOf(
            "Music is the space between the notes",
            "The one who says he can, and the one who says he cannot are both right",
            "Peace of mind is the highest form of wealth",
            "Either you face your demons, or they raise your children",
            "At the end of the game, the king and the pawn go back in the same box",
            "Forgive others, not because they deserve forgiveness, but because you deserve peace",
            "The axe forgets, but the tree remembers",
            "The present moment is the only one that exists. Stop living in yesterday's regrets or tomorrow's worries",
        )
    ),
    MoodCategory(
        name = "Roast Me",
        phrases = listOf(
            "No roast today. Be kind, for everyone you meet is fighting a hard battle",
            "This is supposed to be a Roast category. It's not, go get inspired",
            "Edit these categories, make them something beautiful - make them your own",
            "Still here? Character is what we do when no one is watching",
        )
    )
)
