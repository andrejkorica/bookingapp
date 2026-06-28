<script setup lang="ts">
type ReviewVote = "UP" | "DOWN" | null;

type ListingReview = {
  id: number;
  userName: string;
  rating: number;
  comment: string;
  createdAt: string;
  upvotes: number;
  downvotes: number;
  currentUserVote: ReviewVote;
};

defineProps<{
  review: ListingReview;
  isLoggedIn: boolean;
}>();

const emit = defineEmits<{
  vote: [reviewId: number, voteType: "UP" | "DOWN"];
}>();
</script>

<template>
  <UCard class="border border-slate-200">
    <div class="flex items-start justify-between gap-4">
      <div>
        <p class="font-semibold text-slate-900">
          {{ review.userName }}
        </p>

        <p class="mt-1 text-sm text-slate-500">
          {{ new Date(review.createdAt).toLocaleDateString() }}
        </p>
      </div>

      <div class="flex items-center">
        <UIcon
          v-for="i in 5"
          :key="i"
          name="i-heroicons-star-solid"
          :class="i <= review.rating ? 'text-yellow-400' : 'text-slate-200'"
          class="h-4 w-4"
        />
      </div>
    </div>

    <p class="mt-4 leading-relaxed text-slate-600">
      {{ review.comment }}
    </p>

    <div class="mt-4 flex items-center gap-2 border-t border-slate-100 pt-3">
      <UButton
        icon="i-lucide-thumbs-up"
        size="xs"
        :color="review.currentUserVote === 'UP' ? 'primary' : 'neutral'"
        :variant="review.currentUserVote === 'UP' ? 'soft' : 'ghost'"
        :disabled="!isLoggedIn"
        @click="emit('vote', review.id, 'UP')"
      />

      <span class="text-sm text-slate-600">
        {{ review.upvotes }}
      </span>

      <UButton
        icon="i-lucide-thumbs-down"
        size="xs"
        :color="review.currentUserVote === 'DOWN' ? 'error' : 'neutral'"
        :variant="review.currentUserVote === 'DOWN' ? 'soft' : 'ghost'"
        :disabled="!isLoggedIn"
        @click="emit('vote', review.id, 'DOWN')"
      />

      <span class="text-sm text-slate-600">
        {{ review.downvotes }}
      </span>
    </div>
  </UCard>
</template>