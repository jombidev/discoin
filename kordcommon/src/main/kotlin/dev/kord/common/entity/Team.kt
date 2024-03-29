package dev.kord.common.entity

import kotlinx.serialization.*

/**
 * The raw developer team data gotten from the API.
 */
@Serializable
public data class DiscordTeam(
    val icon: String?,
    val id: Snowflake,
    val members: List<DiscordTeamMember>,
    @SerialName("owner_user_id")
    val ownerUserId: Snowflake,
)

/**
 * The raw developer team member data gotten from the API.
 */
@Serializable
public data class DiscordTeamMember(
    /**
     * An integer enum representing the state of membership of this user.
     * `1` means the user has been invited and `2` means the user has accepted the invitation.
     */
    @SerialName("membership_state")
    val membershipState: TeamMembershipState,
    /**
     * A collection of permissions granted to this member.
     * At the moment, this collection will only have one element: `*`, meaning the member has all permissions.
     * This is because right now there are no other permissions. Read mode [here](https://discord.com/developers/docs/topics/teams#data-models-team-members-object)
     */
    val permissions: List<String>,
    /**
     * The unique ID that this member belongs to.
     */
    @SerialName("team_id")
    val teamId: Snowflake,
    /**
     * Partial user data containing only the ID, username, discriminator and avatar.
     */
    val user: DiscordUser,
)
