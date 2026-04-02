import { saveAs } from 'file-saver'

/**
 * Format file size
 * @param size
 */
export const formatSize = (size?: number) => {
  if (!size) return 'Unknown'
  if (size < 1024) return size + ' B'
  if (size < 1024 * 1024) return (size / 1024).toFixed(2) + ' KB'
  return (size / (1024 * 1024)).toFixed(2) + ' MB'
}

/**
 * Download image
 * @param url Image download URL
 * @param fileName File name to save as
 */
export function downloadImage(url?: string, fileName?: string) {
  if (!url) {
    return
  }
  saveAs(url, fileName)
}

/**
 * Convert color value to standard #RRGGBB format
 * @param input
 */
export function toHexColor(input: string) {
  // Remove 0x prefix
  const colorValue = input.startsWith('0x') ? input.slice(2) : input

  // Parse remaining part as hex number, convert to 6-digit hex string
  const hexColor = parseInt(colorValue, 16).toString(16).padStart(6, '0')

  // Return standard #RRGGBB format
  return `#${hexColor}`
}
