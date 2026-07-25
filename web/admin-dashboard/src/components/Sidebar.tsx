'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useStore } from '@/lib/store';

interface NavItem {
  href: string;
  icon: string;
  label: string;
  badgeKey?: keyof NonNullable<ReturnType<typeof useStore.getState>['metrics']>;
}

const navItems: NavItem[] = [
  { href: '/dashboard', icon: '📊', label: 'Overview' },
  { href: '/dashboard/shoppers', icon: '👥', label: 'Shoppers' },
  { href: '/dashboard/orders', icon: '📦', label: 'Orders' },
  { href: '/dashboard/disputes', icon: '⚖️', label: 'Disputes', badgeKey: 'openDisputes' },
  { href: '/dashboard/zones', icon: '🌍', label: 'Zones' },
];

export default function Sidebar() {
  const pathname = usePathname();
  const { userName, userRole, metrics, logout } = useStore();

  return (
    <aside className="fixed left-0 top-0 h-full w-64 bg-white shadow-lg flex flex-col">
      <div className="p-6 border-b">
        <h1 className="text-xl font-bold text-green-600">Urban Shopper</h1>
        <p className="text-sm text-gray-500">Admin Dashboard</p>
        {userName && (
          <p className="text-xs text-gray-400 mt-1">
            {userName} ({userRole})
          </p>
        )}
      </div>

      <nav className="flex-1 px-4 py-4 space-y-1">
        {navItems.map((item) => {
          const active = pathname === item.href;
          const badgeCount =
            item.badgeKey && metrics
              ? (metrics[item.badgeKey] as number)
              : undefined;

          return (
            <Link
              key={item.href}
              href={item.href}
              className={`flex items-center justify-between px-4 py-3 rounded-lg transition-colors ${
                active
                  ? 'bg-green-50 text-green-700 font-medium'
                  : 'text-gray-600 hover:bg-gray-50'
              }`}
            >
              <span>
                {item.icon} {item.label}
              </span>
              {badgeCount !== undefined && badgeCount > 0 && (
                <span className="bg-red-500 text-white text-xs px-2 py-0.5 rounded-full">
                  {badgeCount}
                </span>
              )}
            </Link>
          );
        })}
      </nav>

      <div className="p-4 border-t">
        <button
          onClick={logout}
          className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50 rounded-lg"
        >
          🚪 Logout
        </button>
      </div>
    </aside>
  );
}
