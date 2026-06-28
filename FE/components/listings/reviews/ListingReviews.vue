<script setup lang="ts">
import ListingReviewCard from "~/components/listings/reviews/ListingReviewCard.vue";
import type { ListingReview } from "~/types/ReviewTypes";

const props = defineProps<{
  reviews: ListingReview[];
  isLoggedIn: boolean;
}>();

const emit = defineEmits<{
  submitReview: [review: { rating: number; comment: string }];
  voteReview: [reviewId: number, voteType: "UP" | "DOWN"];
}>();

const rating = ref(5);
const comment = ref("");
const reviewSort = ref("newest");

const reviewSortOptions = [
  { label: "Newest", value: "newest" },
  { label: "Oldest", value: "oldest" },
  { label: "Highest rating", value: "highest-rating" },
  { label: "Lowest rating", value: "lowest-rating" },
  { label: "Most upvoted", value: "most-upvotes" },
  { label: "Least upvoted", value: "least-upvotes" },
];

const sortedReviews = computed(() => {
  const list = [...props.reviews];

  switch (reviewSort.value) {
    case "highest-rating":
      return list.sort((a, b) => b.rating - a.rating);

    case "lowest-rating":
      return list.sort((a, b) => a.rating - b.rating);

    case "most-upvotes":
      return list.sort((a, b) => b.upvotes - a.upvotes);

    case "least-upvotes":
      return list.sort((a, b) => a.upvotes - b.upvotes);

    case "oldest":
      return list.sort(
        (a, b) =>
          new Date(a.createdAt).getTime() -
          new Date(b.createdAt).getTime(),
      );

    case "newest":
    default:
      return list.sort(
        (a, b) =>
          new Date(b.createdAt).getTime() -
          new Date(a.createdAt).getTime(),
      );
  }
});

function submitReview() {
  if (!comment.value.trim()) return;

  emit("submitReview", {
    rating: rating.value,
    comment: comment.value.trim(),
  });

  rating.value = 5;
  comment.value = "";
}

function handleVote(reviewId: number, voteType: "UP" | "DOWN") {
  emit("voteReview", reviewId, voteType);
}
</script>

<template>
  <section class="mt-12">
    <div class="mb-6 flex items-center justify-between gap-4 pb-2">
      <h2 class="text-2xl font-bold">
        Reviews and Comments ({{ reviews.length }})
      </h2>

      <USelect
        v-if="reviews.length > 1"
        v-model="reviewSort"
        :items="reviewSortOptions"
        class="w-56"
      />
    </div>

    <UCard v-if="isLoggedIn" class="mb-6 border border-slate-200">
      <div class="space-y-4">
        <div>
          <p class="mb-2 font-medium text-slate-900">Your rating</p>

          <div class="flex items-center gap-1">
            <button
              v-for="i in 5"
              :key="i"
              type="button"
              @click="rating = i"
            >
              <UIcon
                name="i-heroicons-star-solid"
                :class="i <= rating ? 'text-yellow-400' : 'text-slate-300'"
                class="h-6 w-6"
              />
            </button>
          </div>
        </div>

        <UTextarea
          v-model="comment"
          placeholder="Write your comment..."
          autoresize
          class="w-full"
          :ui="{ root: 'w-full', base: 'w-full' }"
        />

        <div class="flex justify-end">
          <UButton
            label="Submit review"
            class="bg-indigo-600 text-white hover:bg-indigo-700"
            :disabled="!comment.trim()"
            @click="submitReview"
          />
        </div>
      </div>
    </UCard>

    <p v-else class="mb-6 text-sm text-slate-500">
      Log in to leave a review.
    </p>

    <div
      v-if="reviews.length === 0"
      class="rounded-xl border border-slate-200 bg-slate-50 p-5 text-slate-500"
    >
      No reviews yet.
    </div>

    <div v-else class="space-y-4">
      <ListingReviewCard
        v-for="review in sortedReviews"
        :key="review.id"
        :review="review"
        :is-logged-in="isLoggedIn"
        @vote="handleVote"
      />
    </div>
  </section>
</template>