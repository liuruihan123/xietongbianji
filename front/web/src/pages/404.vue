<template>
  <!-- page wrap -->
  <div class="section section--content">
    <div class="section__content">
      <!-- error -->
      <div class="error">
        <div class="error__logo-wrap">
          <router-link to="/" class="error__logo">
            <img src="/logo-placeholder.svg" alt="协同编辑">
          </router-link>
          <span class="error__tagline"> <br>协同编辑</span>
        </div>

        <div class="error__content">
          <h1 class="error__title">404</h1>
          <h2 class="error__subtitle">Page Not Found</h2>
          <p class="error__text">
            The page you are looking for might have been removed, 
            had its name changed, or is temporarily unavailable.
          </p>

          <div class="error__actions">
            <router-link to="/" class="error__btn">
              <i class="ti ti-home"></i>
              Go Home
            </router-link>
            <a href="#" @click.prevent="goBack" class="error__btn error__btn--light">
              <i class="ti ti-arrow-left"></i>
              Go Back
            </a>
          </div>

          <div class="error__help">
            <p>Need help? <router-link to="/contacts">Contact our support team</router-link></p>
          </div>
        </div>

        <!-- design elements -->
        <span class="block-icon block-icon--red">
          <i class="ti ti-alert-triangle"></i>
        </span>
        <span class="screw screw--big-tr"></span>
        <span class="screw screw--big-bl"></span>
        <span class="screw screw--big-br"></span>
      </div>
      <!-- end error -->
    </div>

    <!-- <div class="section__canvas section__canvas--full section__canvas--third" id="canvas3"></div> -->
  </div>
  <!-- end page wrap -->
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  if (window.history.length > 1) {
    router.go(-1)
  } else {
    router.push('/')
  }
}
</script>

<style scoped>
.error {
  position: relative;
  max-width: 600px;
  margin: 0 auto;
  padding: 3rem 2rem;
  text-align: center;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  backdrop-filter: blur(10px);
}

.error__logo-wrap {
  margin-bottom: 3rem;
}

.error__logo {
  display: inline-block;
  margin-bottom: 1rem;
}

.error__logo img {
  height: 60px;
}

.error__tagline {
  display: block;
  color: rgba(255, 255, 255, 0.7);
  font-size: 0.9rem;
  line-height: 1.4;
}

.error__content {
  margin-bottom: 2rem;
}

.error__title {
  font-size: 8rem;
  font-weight: 700;
  color: var(--main-color, #00d4ff);
  margin-bottom: 1rem;
  line-height: 1;
  text-shadow: 0 0 30px rgba(0, 212, 255, 0.5);
}

.error__subtitle {
  font-size: 2rem;
  font-weight: 600;
  color: #fff;
  margin-bottom: 1rem;
}

.error__text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 1.1rem;
  line-height: 1.6;
  margin-bottom: 2rem;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.error__actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
  margin-bottom: 2rem;
}

.error__btn {
  display: inline-flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: var(--main-color, #00d4ff);
  color: #000;
  text-decoration: none;
  border-radius: 5px;
  font-weight: 600;
  transition: all 0.3s;
  border: 2px solid var(--main-color, #00d4ff);
}

.error__btn--light {
  background: transparent;
  color: var(--main-color, #00d4ff);
}

.error__btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 212, 255, 0.3);
}

.error__btn--light:hover {
  background: var(--main-color, #00d4ff);
  color: #000;
}

.error__help {
  padding-top: 1rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.error__help p {
  color: rgba(255, 255, 255, 0.7);
  margin: 0;
}

.error__help a {
  color: var(--main-color, #00d4ff);
  text-decoration: none;
}

.error__help a:hover {
  text-decoration: underline;
}

/* Design elements */
.block-icon--red {
  background: #ff4d4f;
}

/* Responsive design */
@media (max-width: 768px) {
  .error {
    padding: 2rem 1rem;
  }
  
  .error__title {
    font-size: 6rem;
  }
  
  .error__subtitle {
    font-size: 1.5rem;
  }
  
  .error__actions {
    flex-direction: column;
    align-items: center;
  }
  
  .error__btn {
    width: 100%;
    max-width: 250px;
    justify-content: center;
  }
}
</style>
