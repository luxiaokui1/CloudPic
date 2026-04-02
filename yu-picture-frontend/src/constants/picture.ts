/**
 * Picture review status
 */
export const PIC_REVIEW_STATUS_ENUM = {
  REVIEWING: 0,
  PASS: 1,
  REJECT: 2,
}

/**
 * Picture review status labels
 */
export const PIC_REVIEW_STATUS_MAP = {
  0: 'Pending Review',
  1: 'Approved',
  2: 'Rejected',
}

/**
 * Picture review status dropdown options
 */
export const PIC_REVIEW_STATUS_OPTIONS = Object.keys(PIC_REVIEW_STATUS_MAP).map((key) => {
  return {
    label: PIC_REVIEW_STATUS_MAP[key],
    value: key,
  }
})

/**
 * Picture edit message type enum
 */
export const PICTURE_EDIT_MESSAGE_TYPE_ENUM = {
  INFO: 'INFO',
  ERROR: 'ERROR',
  ENTER_EDIT: 'ENTER_EDIT',
  EXIT_EDIT: 'EXIT_EDIT',
  EDIT_ACTION: 'EDIT_ACTION',
};

/**
 * Picture edit message type labels
 */
export const PICTURE_EDIT_MESSAGE_TYPE_MAP = {
  INFO: 'Send Notification',
  ERROR: 'Send Error',
  ENTER_EDIT: 'Enter Edit Mode',
  EXIT_EDIT: 'Exit Edit Mode',
  EDIT_ACTION: 'Execute Edit Action',
};

/**
 * Picture edit action enum
 */
export const PICTURE_EDIT_ACTION_ENUM = {
  ZOOM_IN: 'ZOOM_IN',
  ZOOM_OUT: 'ZOOM_OUT',
  ROTATE_LEFT: 'ROTATE_LEFT',
  ROTATE_RIGHT: 'ROTATE_RIGHT',
};

/**
 * Picture edit action labels
 */
export const PICTURE_EDIT_ACTION_MAP = {
  ZOOM_IN: 'Zoom In',
  ZOOM_OUT: 'Zoom Out',
  ROTATE_LEFT: 'Rotate Left',
  ROTATE_RIGHT: 'Rotate Right',
};
