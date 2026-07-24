import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Urban Shopper — Admin',
  description: 'Urban Shopper Platform Administration',
};

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
