import type { RouteLocationRaw } from 'vue-router'

type NoticeLike = {
  id?: number
  noticeType?: number
  bizType?: string
  bizId?: number
  routerPath?: string
}

const normalizePath = (p?: string) => {
  if (!p) return ''
  return String(p).trim()
}

const parseIdFromPathTail = (p: string): number | null => {
  const parts = p.split('/').filter(Boolean)
  if (parts.length === 0) return null
  const last = parts[parts.length - 1]
  const n = Number(last)
  return Number.isFinite(n) && n > 0 ? n : null
}

export function getNoticeLocation(n: NoticeLike): RouteLocationRaw | null {
  const bizType = (n.bizType || '').trim()
  const bizId = Number(n.bizId)
  const hasBizId = Number.isFinite(bizId) && bizId > 0
  const routerPath = normalizePath(n.routerPath)

  if (routerPath) {
    if (routerPath.startsWith('/house/detail/') || routerPath.startsWith('/house/edit/')) return { path: routerPath }
    if (routerPath.startsWith('/project/detail/') || routerPath.startsWith('/project/edit/')) return { path: routerPath }
    if (routerPath.startsWith('/community/detail/') || routerPath.startsWith('/community/edit/')) return { path: routerPath }

    if (routerPath.startsWith('/transaction/detail/')) {
      const id = parseIdFromPathTail(routerPath)
      return id ? { path: '/transaction/list', query: { detailId: String(id) } } : { path: '/transaction/list' }
    }

    // 当前 /transaction/audit 为占位页，改为跳到交易列表并打开编辑弹窗
    if (routerPath === '/transaction/audit') {
      return hasBizId ? { path: '/transaction/list', query: { auditId: String(bizId) } } : { path: '/transaction/list' }
    }

    if (routerPath === '/house/audit') {
      return hasBizId ? { path: '/house/audit', query: { id: String(bizId) } } : { path: '/house/audit' }
    }

    if (routerPath === '/commission/list') {
      return hasBizId ? { path: '/commission/list', query: { highlightId: String(bizId) } } : { path: '/commission/list' }
    }

    if (routerPath === '/community/list') {
      return hasBizId ? { path: '/community/detail/' + String(bizId) } : { path: '/community/list' }
    }

    if (routerPath === '/project/list') {
      return hasBizId ? { path: '/project/detail/' + String(bizId) } : { path: '/project/list' }
    }

    if (routerPath === '/customer/my') {
      return { path: '/customer/list', query: hasBizId ? { id: String(bizId) } : {} }
    }

    if (routerPath === '/house/my') {
      return hasBizId ? { path: '/house/detail/' + String(bizId) } : { path: '/house/list' }
    }

    if (routerPath === '/account/profile') {
      return { path: '/profile' }
    }

    if (routerPath === '/view-record/my') {
      return { path: '/customer/follow' }
    }

    return { path: routerPath }
  }

  if (bizType) {
    if (bizType.includes('transaction') && hasBizId) return { path: '/transaction/list', query: { detailId: String(bizId) } }
    if (bizType.includes('house') && hasBizId) return { path: '/house/detail/' + String(bizId) }
    if (bizType.includes('project') && hasBizId) return { path: '/project/detail/' + String(bizId) }
    if (bizType.includes('community') && hasBizId) return { path: '/community/detail/' + String(bizId) }
    if (bizType.includes('commission')) return { path: '/commission/list', query: hasBizId ? { highlightId: String(bizId) } : {} }
    if (bizType.includes('customer')) return { path: '/customer/list', query: hasBizId ? { id: String(bizId) } : {} }
  }

  return null
}
