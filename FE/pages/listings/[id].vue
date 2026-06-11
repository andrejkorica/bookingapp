<template>
  <div class="bg-white text-slate-900 min-h-screen">
    <UContainer class="py-12">
      <div v-if="listingData">
        <header class="mb-8">
          <h1 class="text-4xl md:text-5xl font-bold tracking-tight text-slate-900 mb-2">
            {{ listingData.title }}
          </h1>
          <div class="flex items-center space-x-4 text-slate-500">
            <div class="flex items-center">
              <UIcon v-for="i in 5" :key="i" name="i-heroicons-star-solid" 
                     :class="i <= listingData.rating ? 'text-yellow-400' : 'text-slate-200'" 
                     class="w-5 h-5" />
            </div>
            <div class="flex items-center">
              <UIcon name="i-heroicons-map-pin" class="w-5 h-5 mr-2" />
              <span>{{ listingData.location }}</span>
            </div>
          </div>
        </header>

        <div class="mb-12">
          <UCarousel 
            v-slot="{ item }" 
            :items="listingData.images"
            :ui="{ item: 'basis-full' }"
            :prev="{color: 'primary'}"
            :next="{color: 'primary'}"
            class="rounded-2xl overflow-hidden shadow-lg"
            arrows
            indicators
          >
            <img :src="item" class="w-full h-96 object-cover" draggable="false">
          </UCarousel>
        </div>

        <div class="grid grid-cols-1 lg:grid-cols-3 gap-12">
          <div class="lg:col-span-2">
            <h2 class="text-2xl font-bold border-b border-slate-200 pb-2 mb-4">
              O objektu
            </h2>
            <p class="text-slate-600 leading-relaxed mb-8">
              {{ listingData.description }}
            </p>

            <h3 class="text-xl font-bold mb-4">Sadržaji</h3>
            <ul class="grid grid-cols-2 gap-x-8 gap-y-2">
              <li v-for="amenity in listingData.amenities" :key="amenity" class="flex items-center">
                <UIcon name="i-heroicons-check-circle" class="w-5 h-5 text-indigo-500 mr-3" />
                <span class="text-slate-600">{{ amenity }}</span>
              </li>
            </ul>
          </div>

          <div>
            <UCard class="bg-white shadow-lg border border-slate-200">
              <div class="text-center space-y-4">
                <p class="text-lg text-slate-500">Cijena po noćenju</p>
                <p class="text-4xl font-bold text-slate-900">
                  €{{ listingData.pricePerNight }}
                </p>
                <UButton 
                  label="Rezerviraj sada"
                  size="xl" 
                  block
                  class="bg-indigo-600 hover:bg-indigo-700 text-white font-bold"
                />
              </div>
            </UCard>
          </div>
        </div>
        
        <div class="mt-16">
          <h2 class="text-2xl font-bold border-b border-slate-200 pb-2 mb-6">
            Recenzije i komentari ({{ listingData.comments.length }})
          </h2>
          <div class="space-y-6">
            <div v-for="(comment, index) in listingData.comments" :key="index"
                 class="p-5 border border-slate-200 rounded-xl bg-slate-50">
              <div class="flex items-center justify-between mb-2">
                <p class="font-semibold text-slate-900">{{ comment.author }}</p>
                <div class="flex items-center">
                  <UIcon v-for="i in 5" :key="i" name="i-heroicons-star-solid" 
                         :class="i <= comment.rating ? 'text-yellow-400' : 'text-slate-200'" 
                         class="w-4 h-4" />
                </div>
              </div>
              <p class="text-sm text-slate-400 mb-1">{{ comment.date }}</p>
              <p class="text-slate-600">{{ comment.text }}</p>
            </div>
          </div>
        </div>

      </div>
      <div v-else>
        <p>Učitavanje podataka o listingu...</p>
      </div>
    </UContainer>
  </div>
</template>

<script setup lang="ts">

const route = useRoute();
const id = route.params.id;

const listingData = {
  id: id,
  title: 'Hotel Ambasador',
  location: 'Trumbićeva obala 18, 21000 Split, Croatia',
  rating: 5,
  pricePerNight: 250,
  images: [
    'https://picsum.photos/id/103/1280/720',
    'https://picsum.photos/id/104/1280/720',
    'https://picsum.photos/id/106/1280/720',
    'https://picsum.photos/id/108/1280/720',
  ],
  description: 'Hotel Ambasador u Splitu nudi krovni bazen, spa sadržaje, saunu, fitness centar, sunčanu terasu, restoran, bar i besplatan WiFi. Gosti mogu uživati u modernom restoranu koji poslužuje mediteransku kuhinju s vegetarijanskim, veganskim, bezglutenskim i bezmliječnim opcijama.',
  amenities: [
    'Krovni bazen',
    'Spa i wellness centar',
    'Besplatan WiFi',
    'Restoran i bar',
    'Fitness centar',
    'Pogled na more',
    'Klimatizacija',
    'Privatna kupaonica'
  ],
  comments: [
    { author: 'Ana Kovačić', rating: 5, date: '25. rujna 2025.', text: 'Apsolutno savršeno! Pogled je nevjerojatan, a osoblje izuzetno ljubazno. Bazen na krovu je highlight boravka.' },
    { author: 'Marko Jurić', rating: 4, date: '15. rujna 2025.', text: 'Vrlo dobar hotel na odličnoj lokaciji. Soba je bila prostrana i čista. Jedina zamjerka je što je doručak mogao biti malo raznovrsniji.' },
    { author: 'Ivana Babić', rating: 5, date: '02. rujna 2025.', text: 'Preporučujem svima! Moderno uređen, čist, s fantastičnim spa centrom. Vraćamo se sigurno.' }
  ]
};


useHead({
  title: `${listingData.title} | Detalji`
})
</script>

<style scoped>

</style>