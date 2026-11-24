<template>
  <footer class="footer">
    <div class="footer__content">
      <div class="footer__copyright">
        <!-- <span class="copyright__text">© 2025 汇众志远技术有限公司</span> -->
        <span class="copyright__divider">•</span>
        <!-- <a href="#" class="copyright__link">隐私政策</a>
        <span class="copyright__divider">•</span>
        <a href="#" class="copyright__link">服务条款</a> -->
      </div>
    </div>
  </footer>
</template>

<script setup lang="ts">
// Footer component logic can be added here if needed
</script>

<style scoped lang="scss">
.footer {
  width: 100%;
  background: linear-gradient(to bottom, transparent, rgba(0, 0, 0, 0.02));
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
  padding: 12px 0;
  margin-top: auto;

  &__content {
    display: flex;
    align-items: center;
    justify-content: center;
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 20px;
  }

  &__copyright {
    display: flex;
    align-items: center;
    gap: 12px;
    font-size: 12px;
    color: rgba(0, 0, 0, 0.55);
    white-space: nowrap;
    transition: all 0.3s ease;

    &:hover {
      color: rgba(0, 0, 0, 0.75);
    }
  }
}

.copyright {
  &__text {
    font-weight: 500;
    letter-spacing: 0.3px;
  }

  &__divider {
    opacity: 0.4;
    font-weight: 300;
  }

  &__link {
    color: rgba(0, 0, 0, 0.55);
    text-decoration: none;
    transition: all 0.2s ease;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: -2px;
      left: 0;
      right: 0;
      height: 1px;
      background: currentColor;
      opacity: 0;
      transition: opacity 0.2s ease;
    }

    &:hover {
      color: rgba(0, 0, 0, 0.85);

      &::after {
        opacity: 1;
      }
    }
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .footer {
    padding: 10px 0;

    &__content {
      padding: 0 15px;
    }

    &__copyright {
      font-size: 11px;
      gap: 8px;
    }
  }

  .copyright__divider {
    display: none;
  }
}

@media (max-width: 480px) {
  .footer {
    padding: 8px 0;

    &__copyright {
      font-size: 10px;
      flex-direction: column;
      gap: 4px;
    }
  }

  .copyright__link {
    display: none;
  }
}

/* 暗黑模式适配 */
@media (prefers-color-scheme: dark) {
  .footer {
    border-top-color: rgba(255, 255, 255, 0.1);
    background: linear-gradient(to bottom, transparent, rgba(255, 255, 255, 0.02));
  }

  .copyright {
    &__text {
      color: rgba(255, 255, 255, 0.65);
    }

    &__divider {
      color: rgba(255, 255, 255, 0.3);
    }

    &__link {
      color: rgba(255, 255, 255, 0.65);

      &:hover {
        color: rgba(255, 255, 255, 0.95);
      }
    }
  }

  .footer {
    &__copyright {
      color: rgba(255, 255, 255, 0.65);

      &:hover {
        color: rgba(255, 255, 255, 0.85);
      }
    }
  }
}
</style>
