<template>
  <div class="w-full px-2 py-2 overflow-hidden relative">
    <div class="flex w-full">
      <div class="h-full relative" :style="{ width: '50px' }">
        <covers-book-cover :library-item="libraryItem" :width="50" :book-cover-aspect-ratio="bookCoverAspectRatio" />
      </div>
      <div class="flex-grow flex-grow-stacked item-table-content h-full px-2 flex items-center">
        <div class="max-w-full">
          <nuxt-link :to="`/item/${libraryItem.id}`" class="truncate block text-sm">{{ itemTitle }}</nuxt-link>
          <p class="truncate block text-gray-400 text-xs">{{ bookAuthorName }}</p>
        </div>
        <div
          class="h-8 px-4 border border-white border-opacity-20 hover:bg-white hover:bg-opacity-10 rounded-full flex items-center justify-center cursor-pointer"
          :class="userIsFinished ? 'text-white text-opacity-40' : ''" @click.stop="playClick">
          <span class="material-icons" :class="streamIsPlaying ? '' : 'text-success'">{{ streamIsPlaying ? 'pause' :
            'play_arrow' }}</span>
          <p class="pl-2 pr-1 text-sm font-semibold">{{ timeRemaining }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  props: {
    playlistId: String,
    item: {
      type: Object,
      default: () => { }
    }
  },
  data() {
    return {
      isProcessingReadUpdate: false,
      processingRemove: false
    }
  },
  computed: {
    libraryItem() {
      return this.item.libraryItem || {}
    },
    episode() {
      return this.item.episode
    },
    episodeId() {
      return this.episode ? this.episode.id : null
    },
    media() {
      return this.libraryItem.media || {}
    },
    mediaMetadata() {
      return this.media.metadata || {}
    },
    tracks() {
      if (this.episode) return []
      return this.media.tracks || []
    },
    itemTitle() {
      if (this.episode) return this.episode.title
      return this.mediaMetadata.title || ''
    },
    bookAuthors() {
      if (this.episode) return []
      return this.mediaMetadata.authors || []
    },
    bookAuthorName() {
      return this.bookAuthors.map((au) => au.name).join(', ')
    },
    itemDuration() {
      if (this.episode) return this.$elapsedPretty(this.episode.duration)
      return this.$elapsedPretty(this.media.duration)
    },
    isMissing() {
      return this.libraryItem.isMissing
    },
    isInvalid() {
      return this.libraryItem.isInvalid
    },
    bookCoverAspectRatio() {
      return this.$store.getters['libraries/getBookCoverAspectRatio']
    },
    coverWidth() {
      return 50
    },
    isMissing() {
      return this.libraryItem.isMissing
    },
    isInvalid() {
      return this.libraryItem.isInvalid
    },
    isStreaming() {
      return this.$store.getters['getIsItemStreaming'](this.item.id)
    },
    localMediaProgress() {
      if (this.isLocal) return this.$store.getters['globals/getLocalMediaProgressById'](this.libraryItemId, this.episode.id)
      else if (this.localLibraryItemId && this.localEpisode) {
        return this.$store.getters['globals/getLocalMediaProgressById'](this.localLibraryItemId, this.localEpisode.id)
      } else {
        return null
      }
    },
    streamIsPlaying() {
      return this.$store.state.playerIsPlaying && this.isStreaming
    },
    itemProgressPercent() {
      return this.itemProgress ? this.itemProgress.progress : 0
    },
    userIsFinished() {
      return this.itemProgress ? !!this.itemProgress.isFinished : false
    },
    localEpisodeId() {
      return this.localEpisode ? this.localEpisode.id : null
    },
    goToEpisodePage() {
      this.$router.push(`/item/${this.libraryItemId}/${this.episode.id}`)
    },
    timeRemaining() {
      if (this.streamIsPlaying) return 'Playing'
      if (!this.itemProgressPercent) return this.$elapsedPretty(this.episode.duration)
      if (this.userIsFinished) return 'Finished'
      var remaining = Math.floor(this.itemProgress.duration - this.itemProgress.currentTime)
      return `${this.$elapsedPretty(remaining)} left`
    },
    showPlayBtn() {
      return !this.isMissing && !this.isInvalid && !this.isStreaming && (this.tracks.length || this.episode)
    }
  },
  methods: {
    async playClick() {
      await this.$hapticsImpact()
      if (this.streamIsPlaying) {
        this.$eventBus.$emit('pause-item')
      } else {
        if (this.localEpisode && this.localLibraryItemId) {
          console.log('Play local episode', this.localEpisode.id, this.localLibraryItemId)

          this.$eventBus.$emit('play-item', {
            libraryItemId: this.localLibraryItemId,
            episodeId: this.localEpisodeId,
            serverLibraryItemId: this.libraryItem.id,
            serverEpisodeId: this.episodeId
          })
        } else {
          this.$eventBus.$emit('play-item', {
            libraryItemId: this.libraryItem.id,
            episodeId: this.episodeId
          })
        }
      }
    },
  },
  mounted() { }
}
</script>

<style>
.item-table-content {
  width: calc(100% - 50px);
  max-width: calc(100% - 50px);
}
</style>
