export default class PictureEditWebSocket {
  private pictureId: number
  private socket: WebSocket | null
  private eventHandlers: any

  constructor(pictureId: number) {
    this.pictureId = pictureId // Current picture ID being edited
    this.socket = null // WebSocket instance
    this.eventHandlers = {} // Custom event handlers
  }

  /**
   * Initialize WebSocket connection
   */
  connect() {
    const DEV_BASE_URL = "ws://localhost:8123";
    // Production URL
    // const PROD_BASE_URL = "ws://81.69.229.63";
    const url = `${DEV_BASE_URL}/api/ws/picture/edit?pictureId=${this.pictureId}`
    this.socket = new WebSocket(url)

    // Set binary type
    this.socket.binaryType = 'blob'

    // Listen for connection open event
    this.socket.onopen = () => {
      console.log('WebSocket connection established')
      this.triggerEvent('open')
    }

    // Listen for message event
    this.socket.onmessage = (event) => {
      const message = JSON.parse(event.data)
      console.log('Message received:', message)

      // Trigger corresponding event based on message type
      const type = message.type
      this.triggerEvent(type, message)
    }

    // Listen for connection close event
    this.socket.onclose = (event) => {
      console.log('WebSocket connection closed:', event)
      this.triggerEvent('close', event)
    }

    // Listen for error event
    this.socket.onerror = (error) => {
      console.error('WebSocket error:', error)
      this.triggerEvent('error', error)
    }
  }

  /**
   * Close WebSocket connection
   */
  disconnect() {
    if (this.socket) {
      this.socket.close()
      console.log('WebSocket connection manually closed')
    }
  }

  /**
   * Send message to backend
   * @param {Object} message Message object
   */
  sendMessage(message: object) {
    if (this.socket && this.socket.readyState === WebSocket.OPEN) {
      this.socket.send(JSON.stringify(message))
      console.log('Message sent:', message)
    } else {
      console.error('WebSocket not connected, cannot send message:', message)
    }
  }

  /**
   * Add custom event listener
   * @param {string} type Message type
   * @param {Function} handler Message handler function
   */
  on(type: string, handler: (data?: any) => void) {
    if (!this.eventHandlers[type]) {
      this.eventHandlers[type] = []
    }
    this.eventHandlers[type].push(handler)
  }

  /**
   * Trigger event
   * @param {string} type Message type
   * @param {Object} data Message data
   */
  triggerEvent(type: string, data?: any) {
    const handlers = this.eventHandlers[type]
    if (handlers) {
      handlers.forEach((handler: any) => handler(data))
    }
  }
}
