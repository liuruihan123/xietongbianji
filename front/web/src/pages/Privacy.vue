<template>
  <MainLayout>
    <!-- head -->
    <div class="section section--head">
      <div class="container">
        <div class="row">
          <!-- title -->
          <div class="col-12 col-md-8 offset-md-2 col-lg-6 offset-lg-3">
            <div class="section__title">
              <h1>Privacy Policy & Terms</h1>
              <p>Your privacy and security are our top priorities. Please review our policies carefully.</p>
            </div>
          </div>
          <!-- end title -->
        </div>
      </div>
    </div>
    <!-- end head -->

    <!-- privacy content -->
    <div class="section">
      <div class="container">
        <div class="row">
          <div class="col-12">
            <a-tabs v-model:activeKey="activeTab" class="privacy-tabs">
              <a-tab-pane key="privacy" tab="Privacy Policy">
                <div class="privacy-content">
                  <h2>Privacy Policy</h2>
                  <p class="last-updated">Last updated: {{ lastUpdated }}</p>

                  <div class="privacy-section">
                    <h3>1. Information We Collect</h3>
                    <p>We collect information you provide directly to us, such as when you create an account, make investments, or contact us for support.</p>
                    
                    <h4>Personal Information:</h4>
                    <ul>
                      <li>Name, email address, and contact information</li>
                      <li>Government-issued identification for verification</li>
                      <li>Financial information for transactions</li>
                      <li>Investment preferences and risk tolerance</li>
                    </ul>

                    <h4>Automatically Collected Information:</h4>
                    <ul>
                      <li>Device information and IP address</li>
                      <li>Usage data and analytics</li>
                      <li>Cookies and similar technologies</li>
                    </ul>
                  </div>

                  <div class="privacy-section">
                    <h3>2. How We Use Your Information</h3>
                    <p>We use the information we collect to:</p>
                    <ul>
                      <li>Provide and maintain our services</li>
                      <li>Process transactions and manage your account</li>
                      <li>Comply with legal and regulatory requirements</li>
                      <li>Improve our platform and user experience</li>
                      <li>Communicate with you about your account and our services</li>
                      <li>Detect and prevent fraud and security threats</li>
                    </ul>
                  </div>

                  <div class="privacy-section">
                    <h3>3. Information Sharing</h3>
                    <p>We do not sell, trade, or rent your personal information to third parties. We may share your information only in the following circumstances:</p>
                    <ul>
                      <li>With your explicit consent</li>
                      <li>To comply with legal obligations</li>
                      <li>With trusted service providers who assist in our operations</li>
                      <li>To protect our rights and prevent fraud</li>
                    </ul>
                  </div>

                  <div class="privacy-section">
                    <h3>4. Data Security</h3>
                    <p>We implement industry-standard security measures to protect your information:</p>
                    <ul>
                      <li>End-to-end encryption for all data transmission</li>
                      <li>Secure data storage with regular backups</li>
                      <li>Multi-factor authentication</li>
                      <li>Regular security audits and monitoring</li>
                    </ul>
                  </div>

                  <div class="privacy-section">
                    <h3>5. Your Rights</h3>
                    <p>You have the right to:</p>
                    <ul>
                      <li>Access and review your personal information</li>
                      <li>Request corrections to inaccurate data</li>
                      <li>Delete your account and associated data</li>
                      <li>Opt-out of marketing communications</li>
                      <li>Data portability where applicable</li>
                    </ul>
                  </div>

                  <div class="privacy-section">
                    <h3>6. Contact Us</h3>
                    <p>If you have questions about this Privacy Policy, please contact us:</p>
                    <ul>
                      <li>Email: privacy@zuleed.com</li>
                      <li>Phone: +1 (555) 123-4567</li>
                      <li>Address: 123 Financial District, London, UK</li>
                    </ul>
                  </div>
                </div>
              </a-tab-pane>

              <a-tab-pane key="terms" tab="Terms of Service">
                <div class="privacy-content">
                  <h2>Terms of Service</h2>
                  <p class="last-updated">Last updated: {{ lastUpdated }}</p>

                  <div class="privacy-section">
                    <h3>1. Acceptance of Terms</h3>
                    <p>By accessing and using 协同编辑系统 's services, you accept and agree to be bound by the terms and provision of this agreement.</p>
                  </div>

                  <div class="privacy-section">
                    <h3>2. Description of Service</h3>
                    <p>协同编辑系统  provides cryptocurrency arbitrage trading services using advanced AI algorithms to identify and execute profitable trades across multiple exchanges.</p>
                  </div>

                  <div class="privacy-section">
                    <h3>3. User Responsibilities</h3>
                    <p>As a user of our platform, you agree to:</p>
                    <ul>
                      <li>Provide accurate and complete information</li>
                      <li>Maintain the security of your account credentials</li>
                      <li>Comply with all applicable laws and regulations</li>
                      <li>Not engage in fraudulent or illegal activities</li>
                      <li>Understand the risks associated with cryptocurrency trading</li>
                    </ul>
                  </div>

                  <div class="privacy-section">
                    <h3>4. Investment Risks</h3>
                    <p>Cryptocurrency trading involves substantial risk of loss. You acknowledge that:</p>
                    <ul>
                      <li>Past performance does not guarantee future results</li>
                      <li>Market conditions can change rapidly</li>
                      <li>You may lose some or all of your investment</li>
                      <li>Our platform does not guarantee profits</li>
                    </ul>
                  </div>

                  <div class="privacy-section">
                    <h3>5. Fees and Charges</h3>
                    <p>Our fee structure includes:</p>
                    <ul>
                      <li>Performance fees on successful trades</li>
                      <li>Withdrawal fees based on payment method</li>
                      <li>No hidden charges or monthly fees</li>
                      <li>Transparent fee disclosure in your dashboard</li>
                    </ul>
                  </div>

                  <div class="privacy-section">
                    <h3>6. Limitation of Liability</h3>
                    <p>协同编辑系统  shall not be liable for any indirect, incidental, special, consequential, or punitive damages resulting from your use of our services.</p>
                  </div>

                  <div class="privacy-section">
                    <h3>7. Termination</h3>
                    <p>We reserve the right to terminate or suspend your account at any time for violation of these terms or for any other reason at our sole discretion.</p>
                  </div>

                  <div class="privacy-section">
                    <h3>8. Governing Law</h3>
                    <p>These terms shall be governed by and construed in accordance with the laws of the United Kingdom.</p>
                  </div>
                </div>
              </a-tab-pane>

              <a-tab-pane key="cookies" tab="Cookie Policy">
                <div class="privacy-content">
                  <h2>Cookie Policy</h2>
                  <p class="last-updated">Last updated: {{ lastUpdated }}</p>

                  <div class="privacy-section">
                    <h3>What Are Cookies</h3>
                    <p>Cookies are small text files that are stored on your device when you visit our website. They help us provide you with a better user experience.</p>
                  </div>

                  <div class="privacy-section">
                    <h3>Types of Cookies We Use</h3>
                    
                    <h4>Essential Cookies:</h4>
                    <p>These cookies are necessary for the website to function properly and cannot be disabled.</p>
                    
                    <h4>Analytics Cookies:</h4>
                    <p>These cookies help us understand how visitors interact with our website by collecting and reporting information anonymously.</p>
                    
                    <h4>Functional Cookies:</h4>
                    <p>These cookies enable enhanced functionality and personalization, such as remembering your preferences.</p>
                    
                    <h4>Marketing Cookies:</h4>
                    <p>These cookies are used to track visitors across websites to display relevant advertisements.</p>
                  </div>

                  <div class="privacy-section">
                    <h3>Managing Cookies</h3>
                    <p>You can control and manage cookies in various ways:</p>
                    <ul>
                      <li>Browser settings to block or delete cookies</li>
                      <li>Opt-out tools provided by advertising networks</li>
                      <li>Our cookie preference center</li>
                    </ul>
                  </div>
                </div>
              </a-tab-pane>
            </a-tabs>
          </div>
        </div>
      </div>
    </div>
    <!-- end privacy content -->

    <!-- contact section -->
    <section class="section">
      <div class="container">
        <div class="row">
          <div class="col-12 col-md-8 offset-md-2 col-lg-6 offset-lg-3">
            <div class="legal-contact">
              <h2>Questions About Our Policies?</h2>
              <p>If you have any questions about our Privacy Policy, Terms of Service, or Cookie Policy, please don't hesitate to contact us.</p>
              
              <div class="legal-contact__actions">
                <router-link to="/contacts" class="legal-contact__btn">
                  Contact Legal Team
                </router-link>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
  </MainLayout>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import MainLayout from '../components/layout/MainLayout.vue'

const activeTab = ref('privacy')
const lastUpdated = ref('December 15, 2023')
</script>

<style scoped>
.privacy-tabs {
  background: transparent;
}

:deep(.ant-tabs-nav) {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 10px;
  padding: 0.5rem;
  margin-bottom: 2rem;
}

:deep(.ant-tabs-tab) {
  color: rgba(255, 255, 255, 0.7);
  border: none;
  background: transparent;
  border-radius: 5px;
  margin: 0 0.25rem;
}

:deep(.ant-tabs-tab-active) {
  background: var(--main-color, #00d4ff);
  color: #000;
}

:deep(.ant-tabs-tab:hover) {
  color: var(--main-color, #00d4ff);
}

:deep(.ant-tabs-tab-active:hover) {
  color: #000;
}

:deep(.ant-tabs-ink-bar) {
  display: none;
}

.privacy-content {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  padding: 3rem;
  backdrop-filter: blur(10px);
}

.privacy-content h2 {
  color: #fff;
  font-size: 2.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.last-updated {
  color: var(--main-color, #00d4ff);
  font-style: italic;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.privacy-section {
  margin-bottom: 3rem;
}

.privacy-section h3 {
  color: var(--main-color, #00d4ff);
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.privacy-section h4 {
  color: #fff;
  font-size: 1.2rem;
  font-weight: 500;
  margin: 1.5rem 0 0.5rem;
}

.privacy-section p {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  margin-bottom: 1rem;
}

.privacy-section ul {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  padding-left: 1.5rem;
}

.privacy-section li {
  margin-bottom: 0.5rem;
}

.legal-contact {
  text-align: center;
  padding: 3rem 2rem;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 15px;
  backdrop-filter: blur(10px);
}

.legal-contact h2 {
  color: #fff;
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 1rem;
}

.legal-contact p {
  color: rgba(255, 255, 255, 0.8);
  font-size: 1.1rem;
  line-height: 1.6;
  margin-bottom: 2rem;
}

.legal-contact__actions {
  margin-top: 2rem;
}

.legal-contact__btn {
  display: inline-block;
  padding: 1rem 2rem;
  background: var(--main-color, #00d4ff);
  color: #000;
  text-decoration: none;
  border-radius: 8px;
  font-weight: 600;
  transition: all 0.3s;
}

.legal-contact__btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.3);
}

@media (max-width: 768px) {
  .privacy-content {
    padding: 2rem 1.5rem;
  }
  
  .privacy-content h2 {
    font-size: 2rem;
  }
  
  .privacy-section h3 {
    font-size: 1.3rem;
  }
}
</style>
