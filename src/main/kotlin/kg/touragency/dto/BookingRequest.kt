package kg.touragency.dto

data class BookingRequest(
    val tourDateId: Long = 0,
    val participants: Int = 1,
    val notes: String = ""
)
