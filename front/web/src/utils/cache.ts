/**
 * 缓存工具类
 */

interface CacheOptions {
  expire?: number // 过期时间（毫秒）
}

interface CacheItem {
  value: any
  expire?: number
}

class Cache {
  private storage: Storage

  constructor(storage: Storage) {
    this.storage = storage
  }

  /**
   * 设置缓存
   */
  set(key: string, value: any, options?: CacheOptions): void {
    const item: CacheItem = {
      value,
      expire: options?.expire ? Date.now() + options.expire : undefined
    }
    this.storage.setItem(key, JSON.stringify(item))
  }

  /**
   * 获取缓存
   */
  get(key: string): any {
    const itemStr = this.storage.getItem(key)
    if (!itemStr) return null

    try {
      const item: CacheItem = JSON.parse(itemStr)
      
      // 检查是否过期
      if (item.expire && Date.now() > item.expire) {
        this.remove(key)
        return null
      }
      
      return item.value
    } catch {
      this.remove(key)
      return null
    }
  }

  /**
   * 获取 JSON 对象
   */
  getJSON(key: string): any {
    const value = this.get(key)
    return value && typeof value === 'object' ? value : null
  }

  /**
   * 设置 JSON 对象
   */
  setJSON(key: string, value: any, options?: CacheOptions): void {
    this.set(key, value, options)
  }

  /**
   * 移除缓存
   */
  remove(key: string): void {
    this.storage.removeItem(key)
  }

  /**
   * 清空所有缓存
   */
  clear(): void {
    this.storage.clear()
  }

  /**
   * 检查缓存是否存在
   */
  has(key: string): boolean {
    return this.get(key) !== null
  }
}

// 创建缓存实例
const cache = {
  local: new Cache(localStorage),
  session: new Cache(sessionStorage)
}

export default cache
