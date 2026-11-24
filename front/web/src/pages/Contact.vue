<template>
  <MainLayout>
    <!-- head -->
    <div class="section section--head">
      <div class="container">
        <div class="row">
          <!-- title -->
          <div class="col-12 col-md-6 offset-md-3 col-xl-4 offset-xl-4">
            <div class="section__title">
              <h1>联系我们</h1>
              <p>
                Our support team is always on call, and ready to help with all
                your questions!
              </p>
            </div>
          </div>
          <!-- end title -->
        </div>
      </div>
    </div>
    <!-- end head -->

    <!-- contact form -->
    <div class="section">
      <div class="container">
        <div class="row row--relative">
          <div class="col-12">
            <form class="form" @submit.prevent="onSubmit">
              <div class="row">
                <div class="col-12 col-md-6 col-xl-3">
                  <div class="form__group">
                    <label for="name" class="form__label">称呼</label>
                    <input
                      id="name"
                      type="text"
                      name="name"
                      class="form__input"
                      placeholder="Full name"
                      v-model="contactForm.name"
                      required
                    />
                  </div>
                </div>

                <div class="col-12 col-md-6 col-xl-3">
                  <div class="form__group">
                    <label for="mail" class="form__label">邮箱</label>
                    <input
                      id="mail"
                      type="email"
                      name="mail"
                      class="form__input"
                      placeholder="example@domain.com"
                      v-model="contactForm.email"
                      required
                    />
                  </div>
                </div>

                <div class="col-12 col-md-6 col-xl-3">
                  <div class="form__group">
                    <label for="phone" class="form__label">手机号</label>
                    <input
                      id="phone"
                      type="text"
                      name="phone"
                      class="form__input"
                      placeholder="800 543 - 2109"
                      v-model="contactForm.phone"
                    />
                  </div>
                </div>

                <div class="col-12 col-md-6 col-xl-3">
                  <div class="form__group">
                    <label for="subject" class="form__label">
                      您是如何知道我们的</label
                    >
                    <select
                      id="subject"
                      name="subject"
                      class="form__input"
                      v-model="contactForm.subject"
                      required
                    >
                      <option value="">Select subject</option>
                      <option value="support">Support</option>
                      <option value="investment">Investment Inquiry</option>
                      <option value="partnership">Partnership</option>
                      <option value="technical">Technical Issue</option>
                      <option value="other">Other</option>
                    </select>
                  </div>
                </div>

                <div class="col-12">
                  <div class="form__group">
                    <label for="message" class="form__label"> 您的留言</label>
                    <textarea
                      id="message"
                      name="message"
                      class="form__textarea"
                      placeholder="Please enter your message..."
                      v-model="contactForm.message"
                      required
                    ></textarea>
                  </div>
                </div>

                <div class="col-12">
                  <button
                    type="submit"
                    class="form__btn form__btn--small"
                    :disabled="submitting"
                  >
                    <span v-if="submitting">Submitting...</span>
                    <span v-else>Submit</span>
                  </button>
                </div>
              </div>

              <!-- design elements -->
              <span class="block-icon block-icon--purple">
                <i class="ti ti-mail-up"></i>
              </span>
              <span class="screw screw--lines-bl"></span>
              <span class="screw screw--lines-br"></span>
              <span class="screw screw--lines-tr"></span>
            </form>
          </div>

          <!-- animation background -->
          <div
            class="section__canvas section__canvas--page section__canvas--first"
            id="canvas"
          ></div>
        </div>
      </div>
    </div>
    <!-- end contact form -->

    <!-- company -->
    <!-- <section class="section">
      <div class="container">
        <div class="row row--relative">
          <div class="col-12">
            <div class="company">
              <h2 class="company__title">Company registration</h2>

              <div class="row">
                <div class="col-12 col-xl-7">
                  <p class="company__text">
                    Integrity, reliability, transparency, flexibility and
                    communication should be the core values of any company that
                    works in the field of investment attraction. Alpha Radars  adheres
                    to absolutely all of these values so that our clients can
                    have confidence and trust in us.
                  </p>
                  <p class="company__text">License No. <b>8597366</b></p>
                </div>

                <div class="col-12 col-xl-4 offset-xl-1">
                  <p class="company__subtitle">Legal address:</p>
                  <p class="company__text">
                    99 Chancery Street, <br />
                    Auckland Central, <br />
                    Auckland, 1010, New Zealand
                  </p>
                </div>
              </div>

              <span class="block-icon block-icon--orange">
                <i class="ti ti-certificate-2"></i>
              </span>
              <span class="screw screw--lines-bl"></span>
              <span class="screw screw--lines-br"></span>
              <span class="screw screw--lines-tr"></span>
            </div>
          </div>

          <div
            class="section__canvas section__canvas--second"
            id="canvas2"
          ></div>
        </div>
      </div>
    </section> -->
    <!-- end company -->

    <!-- contact info -->
    <div class="section">
      <div class="container">
        <div class="row">
          <div
            class="col-12 col-md-6 col-lg-4"
            v-for="info in contactInfo"
            :key="info.title"
          >
            <!-- contact item -->
            <div class="contact-item">
              <div class="contact-item__icon">
                <i :class="info.icon"></i>
              </div>
              <h3 class="contact-item__title">{{ info.title }}</h3>
              <div class="contact-item__content">
                <p v-for="line in info.content" :key="line">{{ line }}</p>
              </div>
            </div>
            <!-- end contact item -->
          </div>
        </div>
      </div>
    </div>
    <!-- end contact info -->
  </MainLayout>
</template>

<script setup lang="ts">
import { ref, reactive } from "vue";
import MainLayout from "../components/layout/MainLayout.vue";

const submitting = ref(false);

const contactForm = reactive({
  name: "",
  email: "",
  phone: "",
  subject: "",
  message: "",
});

const contactInfo = ref([
  {
    icon: "ti ti-mail",
    title: "Email Support",
    content: ["support@bicanmou.com", "Available 24/7"],
  },
  {
    icon: "ti ti-phone",
    title: "Phone Support",
    content: ["+1 (555) 123-4567", "Mon-Fri: 9AM-6PM EST"],
  },
  {
    icon: "ti ti-map-pin",
    title: "Office Address",
    content: ["123 Financial District", "London, UK EC2V 8RF"],
  },
  {
    icon: "ti ti-message-circle",
    title: "Live Chat",
    content: ["Available on website", "24/7 instant support"],
  },
  {
    icon: "ti ti-brand-telegram",
    title: "Telegram",
    content: ["@协同编辑", "Community & Updates"],
  },
  {
    icon: "ti ti-clock",
    title: "Response Time",
    content: ["Email: Within 2 hours", "Phone: Immediate"],
  },
]);

const onSubmit = async () => {
  submitting.value = true;

  try {
    // Basic form validation
    if (
      !contactForm.name ||
      !contactForm.email ||
      !contactForm.subject ||
      !contactForm.message
    ) {
      alert("Please fill in all required fields.");
      return;
    }

    if (contactForm.message.length < 10) {
      alert("Message must be at least 10 characters long.");
      return;
    }

    // Simulate API call
    await new Promise((resolve) => setTimeout(resolve, 2000));

    alert("Message sent successfully! We will get back to you soon.");

    // Reset form
    contactForm.name = "";
    contactForm.email = "";
    contactForm.phone = "";
    contactForm.subject = "";
    contactForm.message = "";
  } catch (error) {
    alert("Failed to send message. Please try again.");
  } finally {
    submitting.value = false;
  }
};
</script>

<style scoped>
.contact-item {
  text-align: center;
  padding: 2rem 1rem;
  margin-bottom: 2rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  transition: transform 0.3s;
}

.contact-item:hover {
  transform: translateY(-5px);
}

.contact-item__icon {
  font-size: 3rem;
  color: var(--main-color, #00d4ff);
  margin-bottom: 1rem;
}

.contact-item__title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
  color: #fff;
}

.contact-item__content p {
  margin-bottom: 0.5rem;
  color: #ccc;
}

.office-hours,
.response-time {
  padding: 2rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  margin-bottom: 2rem;
}

.office-hours__title,
.response-time__title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1.5rem;
  color: #fff;
}

.schedule-item,
.response-item {
  display: flex;
  justify-content: space-between;
  padding: 1rem 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.schedule-item:last-child,
.response-item:last-child {
  border-bottom: none;
}

.schedule-day,
.response-type {
  font-weight: 500;
  color: #fff;
}

.schedule-time,
.response-time-value {
  color: var(--main-color, #00d4ff);
}

/* Company section styles */
.company {
  position: relative;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  margin-bottom: 3rem;
}

.company__title {
  font-size: 2rem;
  font-weight: 600;
  color: #fff;
  margin-bottom: 2rem;
}

.company__subtitle {
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--main-color, #00d4ff);
  margin-bottom: 1rem;
}

.company__text {
  color: #ccc;
  line-height: 1.6;
  margin-bottom: 1rem;
}

.company__text b {
  color: var(--main-color, #00d4ff);
}
</style>
